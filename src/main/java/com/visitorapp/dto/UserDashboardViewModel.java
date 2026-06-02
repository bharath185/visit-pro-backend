package com.visitorapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDashboardViewModel {
    @JsonProperty("TotalVisitors") private Long totalVisitors;
    @JsonProperty("TotalInvited") private Long totalInvited;
    @JsonProperty("TotalAccepted") private Long totalAccepted;
    @JsonProperty("TotalCheckedIn") private Long totalCheckedIn;
    @JsonProperty("TotalCheckedOut") private Long totalCheckedOut;
    @JsonProperty("TodayVisitors") private Long todayVisitors;
    @JsonProperty("TodayCheckIns") private Long todayCheckIns;
    @JsonProperty("TodayCheckOuts") private Long todayCheckOuts;
    @JsonProperty("TotalContacted") private Long totalContacted;

    public Long getTotalVisitors() { return totalVisitors; }
    public void setTotalVisitors(Long v) { this.totalVisitors = v; }
    public Long getTotalInvited() { return totalInvited; }
    public void setTotalInvited(Long v) { this.totalInvited = v; }
    public Long getTotalAccepted() { return totalAccepted; }
    public void setTotalAccepted(Long v) { this.totalAccepted = v; }
    public Long getTotalCheckedIn() { return totalCheckedIn; }
    public void setTotalCheckedIn(Long v) { this.totalCheckedIn = v; }
    public Long getTotalCheckedOut() { return totalCheckedOut; }
    public void setTotalCheckedOut(Long v) { this.totalCheckedOut = v; }
    public Long getTodayVisitors() { return todayVisitors; }
    public void setTodayVisitors(Long v) { this.todayVisitors = v; }
    public Long getTodayCheckIns() { return todayCheckIns; }
    public void setTodayCheckIns(Long v) { this.todayCheckIns = v; }
    public Long getTodayCheckOuts() { return todayCheckOuts; }
    public void setTodayCheckOuts(Long v) { this.todayCheckOuts = v; }
    public Long getTotalContacted() { return totalContacted; }
    public void setTotalContacted(Long v) { this.totalContacted = v; }
}
