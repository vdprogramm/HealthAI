package com.vinh.healthcare.service;

import com.vinh.healthcare.dto.AiTriageRequest;
import com.vinh.healthcare.dto.AiTriageResponse;
import com.vinh.healthcare.dto.DoctorScheduleResponse;
import com.vinh.healthcare.dto.ScheduleResponse;
import com.vinh.healthcare.entity.Doctor;
import com.vinh.healthcare.entity.Schedule;
import com.vinh.healthcare.repository.DoctorRepository;
import com.vinh.healthcare.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiTriageService {

    private final DoctorRepository doctorRepository;
    private final ScheduleRepository scheduleRepository;
    private final String aiServiceUrl;

    public AiTriageService(
            DoctorRepository doctorRepository,
            ScheduleRepository scheduleRepository,
            @Value("${ai.service.url}") String aiServiceUrl
    ) {
        this.doctorRepository = doctorRepository;
        this.scheduleRepository = scheduleRepository;
        this.aiServiceUrl = aiServiceUrl;
    }

    public AiTriageResponse triage(AiTriageRequest request) {

        HttpURLConnection connection = null;

        try {
            System.out.println(
                    "JAVA NHAN: " + request.trieuChung()
            );

            String symptom = request.trieuChung()
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");

            String jsonBody =
                    "{\"trieu_chung\":\"" + symptom + "\"}";

            byte[] bodyBytes =
                    jsonBody.getBytes(StandardCharsets.UTF_8);

            System.out.println(
                    "JAVA GUI FASTAPI: " + jsonBody
            );

            System.out.println(
                    "BODY LENGTH: " + bodyBytes.length
            );

            var url = URI.create(
                    aiServiceUrl + "/api/triage"
            ).toURL();

            connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");

            connection.setDoOutput(true);

            connection.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=UTF-8"
            );

            connection.setRequestProperty(
                    "Accept",
                    "application/json"
            );

            // Gửi Content-Length rõ ràng
            connection.setFixedLengthStreamingMode(
                    bodyBytes.length
            );

            connection.connect();

            try (OutputStream os =
                         connection.getOutputStream()) {

                os.write(bodyBytes);
                os.flush();
            }

            int status =
                    connection.getResponseCode();

            InputStream stream;

            if (status >= 200 && status < 300) {
                stream = connection.getInputStream();
            } else {
                stream = connection.getErrorStream();
            }

            String responseBody;

            if (stream != null) {
                try (InputStream input = stream) {
                    responseBody =
                            new String(
                                    input.readAllBytes(),
                                    StandardCharsets.UTF_8
                            );
                }
            } else {
                responseBody = "";
            }

            System.out.println(
                    "FASTAPI STATUS: " + status
            );

            System.out.println(
                    "FASTAPI RESPONSE: " + responseBody
            );

            if (status < 200 || status >= 300) {
                throw new RuntimeException(
                        "FastAPI lỗi "
                                + status
                                + ": "
                                + responseBody
                );
            }

            String chuyenKhoa =
                    extractJsonValue(
                            responseBody,
                            "chuyen_khoa"
                    );

            String mucDoKhanCap =
                    extractJsonValue(
                            responseBody,
                            "muc_do_khan_cap"
                    );

            String giaiThichNgan =
                    extractJsonValue(
                            responseBody,
                            "giai_thich_ngan"
                    );

            String canhBao =
                    extractJsonValue(
                            responseBody,
                            "canh_bao"
                    );

            List<Doctor> doctors =
                    doctorRepository
                            .findByChuyenKhoaIgnoreCase(
                                    chuyenKhoa
                            );

            List<DoctorScheduleResponse> doctorResponses =
                    doctors.stream()
                            .map(doctor -> {
                                List<Schedule> schedules =
                                        scheduleRepository
                                                .findAvailableSchedules(
                                                        doctor.getId()
                                                );

                                List<ScheduleResponse> scheduleResponses =
                                        schedules.stream()
                                                .map(schedule ->
                                                        new ScheduleResponse(
                                                                schedule.getId(),
                                                                schedule.getNgayKham(),
                                                                schedule.getGioBatDau(),
                                                                schedule.getGioKetThuc()
                                                        )
                                                )
                                                .toList();

                                return new DoctorScheduleResponse(
                                        doctor.getId(),
                                        doctor.getTenBacSi(),
                                        doctor.getChuyenKhoa(),
                                        doctor.getGiaKham(),
                                        scheduleResponses
                                );
                            })
                            .toList();

            return new AiTriageResponse(
                    chuyenKhoa,
                    mucDoKhanCap,
                    giaiThichNgan,
                    canhBao,
                    doctorResponses
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Không thể gọi AI Service: "
                            + e.getMessage(),
                    e
            );

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String extractJsonValue(
            String json,
            String key
    ) {

        Pattern pattern = Pattern.compile(
                "\"" + Pattern.quote(key)
                        + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\""
        );

        Matcher matcher =
                pattern.matcher(json);

        if (!matcher.find()) {
            return "";
        }

        return matcher.group(1)
                .replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\\\", "\\");
    }
}
