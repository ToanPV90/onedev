package io.onedev.server.web.util;

import io.onedev.server.model.Project;

import javax.annotation.Nullable;

public interface ProjectAware {
	
	@Nullable
	Project getProject();
	
}
