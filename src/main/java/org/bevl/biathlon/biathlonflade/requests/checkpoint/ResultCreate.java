package org.bevl.biathlon.biathlonflade.requests.checkpoint;

public class ResultCreate {
    public Integer raceId;
    public String userName;
    public Integer checkPointId;
    public Long resultValue = null;
    public boolean isDNF;
    public Integer startNumber;
    public boolean isTeamResult;
    public boolean isFinish;
    public Integer lap;
    public Integer teamId = null;
    public Integer stageNumber = null;
}
