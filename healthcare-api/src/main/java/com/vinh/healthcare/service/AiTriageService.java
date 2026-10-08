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
import java.io.IOException;
import java.net.SocketTimeoutException;
import com.vinh.healthcare.exception.AiServiceUnavailableException;
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

            String symptom = request.trieuChung()
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");

            String jsonBody =
                    "{\"trieu_chung\":\"" + symptom + "\"}";

            byte[] bodyBytes =
                    jsonBody.getBytes(StandardCharsets.UTF_8);



            var url = URI.create(
                    aiServiceUrl + "/api/triage"
            ).toURL();

            connection =
                    (HttpURLConnection) url.openConnection();

            // Render Free instances may require a cold start. Bound the request to avoid hanging.
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(75000);

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


            if (status < 200 || status >= 300) {
                System.err.println("AI upstream HTTP status: " + status);
                throw new AiServiceUnavailableException("Dịch vụ AI chưa sẵn sàng. Vui lòng thử lại sau.");
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

        } catch (AiServiceUnavailableException e) {
            throw e;
        } catch (SocketTimeoutException e) {
            System.err.println("AI upstream timeout: " + e.getClass().getSimpleName());
            throw new AiServiceUnavailableException("Dịch vụ AI đang khởi động hoặc phản hồi chậm. Vui lòng thử lại.", e);
        } catch (IOException e) {
            System.err.println("AI upstream connection error: " + e.getClass().getSimpleName());
            throw new AiServiceUnavailableException("Không thể kết nối dịch vụ AI. Vui lòng thử lại.", e);
        } catch (Exception e) {
            System.err.println("AI triage internal error: " + e.getClass().getSimpleName());
            throw new AiServiceUnavailableException("Không thể xử lý yêu cầu AI lúc này.", e);
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
