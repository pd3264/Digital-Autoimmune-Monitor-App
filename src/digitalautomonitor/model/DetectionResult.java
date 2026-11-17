package digitalautomonitor.model;

import digitalautomonitor.enums.RiskLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the explainable output of an anomaly detection analysis run.
 */
public class DetectionResult {
    private final String targetUser;
    private final int anomalyScore;
    private final int baselineScore;
    private final int confidenceScore;
    private final RiskLevel riskLevel;
    private final List<String> contributingFactors;
    private final String recommendedAction;
    private final int evaluatedRuleCount;
    private final int triggeredRuleCount;
    private final LocalDateTime timestamp;

    public DetectionResult(String targetUser, int anomalyScore, int baselineScore, int confidenceScore,
                           RiskLevel riskLevel, List<String> contributingFactors, String recommendedAction,
                           int evaluatedRuleCount, int triggeredRuleCount) {
        this.targetUser = targetUser;
        this.anomalyScore = Math.min(100, Math.max(0, anomalyScore));
        this.baselineScore = baselineScore;
        this.confidenceScore = confidenceScore;
        this.riskLevel = riskLevel;
        this.contributingFactors = contributingFactors != null ? contributingFactors : new ArrayList<>();
        this.recommendedAction = recommendedAction;
        this.evaluatedRuleCount = evaluatedRuleCount;
        this.triggeredRuleCount = triggeredRuleCount;
        this.timestamp = LocalDateTime.now();
    }

    public String getTargetUser() {
        return targetUser;
    }

    public int getAnomalyScore() {
        return anomalyScore;
    }

    public int getBaselineScore() {
        return baselineScore;
    }

    public int getConfidenceScore() {
        return confidenceScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public List<String> getContributingFactors() {
        return contributingFactors;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public int getEvaluatedRuleCount() {
        return evaluatedRuleCount;
    }

    public int getTriggeredRuleCount() {
        return triggeredRuleCount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getExplanationSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(riskLevel == RiskLevel.NORMAL ? "NORMAL BEHAVIOUR VERIFIED\n" : "ANOMALY DETECTED\n");
        sb.append("Risk Score: ").append(anomalyScore).append("/100 (").append(riskLevel.getDisplayName()).append(")\n");
        sb.append("Confidence: ").append(confidenceScore).append("% | Baseline Score: ").append(baselineScore).append("\n\n");
        sb.append("Contributing Factors:\n");
        if (contributingFactors.isEmpty()) {
            sb.append("  • All observed metrics within normal biological boundaries.\n");
        } else {
            for (String factor : contributingFactors) {
                sb.append("  ").append(factor).append("\n");
            }
        }
        sb.append("\nRecommended Action:\n\"").append(recommendedAction).append("\"");
        return sb.toString();
    }
}
