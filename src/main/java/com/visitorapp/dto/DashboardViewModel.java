package com.visitorapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DashboardViewModel {
    @JsonProperty("TotalVisitors") private Long totalVisitors;
    @JsonProperty("TodayVisitors") private Long todayVisitors;
    @JsonProperty("TotalAccepted") private Long totalAccepted;
    @JsonProperty("TotalCheckedIn") private Long totalCheckedIn;
    @JsonProperty("TotalCheckedOut") private Long totalCheckedOut;
    @JsonProperty("TotalInvited") private Long totalInvited;
    @JsonProperty("TotalPending") private Long totalPending;

    public Long getTotalVisitors() { return totalVisitors; }
    public void setTotalVisitors(Long v) { this.totalVisitors = v; }
    public Long getTodayVisitors() { return todayVisitors; }
    public void setTodayVisitors(Long v) { this.todayVisitors = v; }
    public Long getTotalAccepted() { return totalAccepted; }
    public void setTotalAccepted(Long v) { this.totalAccepted = v; }
    public Long getTotalCheckedIn() { return totalCheckedIn; }
    public void setTotalCheckedIn(Long v) { this.totalCheckedIn = v; }
    public Long getTotalCheckedOut() { return totalCheckedOut; }
    public void setTotalCheckedOut(Long v) { this.totalCheckedOut = v; }
    public Long getTotalInvited() { return totalInvited; }
    public void setTotalInvited(Long v) { this.totalInvited = v; }
    public Long getTotalPending() { return totalPending; }
    public void setTotalPending(Long v) { this.totalPending = v; }
}
