package com.saygobackend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class GenerateTripPlanRequest {
    @NotBlank(message = "目的地不能为空")
    private String destination;

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @NotNull(message = "人数不能为空")
    @Min(value = 1, message = "人数至少为1")
    @Max(value = 100, message = "人数不能超过100")
    private Integer people;

    @NotNull(message = "最低预算不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "最低预算必须大于0")
    private BigDecimal budgetMin;

    @NotNull(message = "最高预算不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "最高预算必须大于0")
    private BigDecimal budgetMax;

    private List<String> interestTags;
}
