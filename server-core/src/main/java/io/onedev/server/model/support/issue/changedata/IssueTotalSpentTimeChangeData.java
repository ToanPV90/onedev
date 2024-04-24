package io.onedev.server.model.support.issue.changedata;

import io.onedev.server.OneDev;
import io.onedev.server.entitymanager.SettingManager;
import io.onedev.server.model.Group;
import io.onedev.server.model.User;
import io.onedev.server.notification.ActivityDetail;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IssueTotalSpentTimeChangeData extends IssueChangeData {

	private static final long serialVersionUID = 1L;

	private final int oldValue;
	
	private final int newValue;
	
	public IssueTotalSpentTimeChangeData(int oldValue, int newValue) {
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Override
	public boolean isMinor() {
		return true;
	}

	@Override
	public String getActivity() {
		return "changed total spent time";
	}

	@Override
	public Map<String, Collection<User>> getNewUsers() {
		return new HashMap<>();
	}

	@Override
	public Map<String, Group> getNewGroups() {
		return new HashMap<>();
	}

	@Override
	public boolean affectsListing() {
		return false;
	}

	@Override
	public ActivityDetail getActivityDetail() {
		var timeTrackingSetting = OneDev.getInstance(SettingManager.class).getIssueSetting().getTimeTrackingSetting();
		Map<String, String> oldFieldValues = new HashMap<>();
		oldFieldValues.put("Total Spent Time", timeTrackingSetting.formatWorkingPeriod(oldValue));
		Map<String, String> newFieldValues = new HashMap<>();
		newFieldValues.put("Total Spent Time", timeTrackingSetting.formatWorkingPeriod(newValue));
		return ActivityDetail.compare(oldFieldValues, newFieldValues, true);
	}
	
}
