package io.onedev.server.model.support.code;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import javax.validation.constraints.NotEmpty;

import io.onedev.commons.codeassist.InputSuggestion;
import io.onedev.server.model.Project;
import io.onedev.server.util.reviewrequirement.ReviewRequirement;
import io.onedev.server.validation.Validatable;
import io.onedev.server.annotation.ClassValidating;
import io.onedev.server.annotation.Editable;
import io.onedev.server.annotation.JobChoice;
import io.onedev.server.annotation.Patterns;
import io.onedev.server.web.util.SuggestionUtils;

@Editable
@ClassValidating
public class FileProtection implements Serializable, Validatable {

	private static final long serialVersionUID = 1L;
	
	private String paths;
	
	private String reviewRequirement;
	
	private transient ReviewRequirement parsedReviewRequirement;
	
	private List<String> jobNames = new ArrayList<>();
	
	@Editable(order=100, description="Specify space-separated paths to be protected. Use '**', '*' or '?' for <a href='https://docs.onedev.io/appendix/path-wildcard' target='_blank'>path wildcard match</a>. "
			+ "Prefix with '-' to exclude")
	@Patterns(suggester = "suggestPaths", path=true)
	@NotEmpty
	public String getPaths() {
		return paths;
	}

	public void setPaths(String paths) {
		this.paths = paths;
	}
	
	@SuppressWarnings("unused")
	private static List<InputSuggestion> suggestPaths(String matchWith) {
		if (Project.get() != null)
			return SuggestionUtils.suggestBlobs(Project.get(), matchWith);
		else
			return new ArrayList<>();
	}

	@Editable(order=200, name="Reviewers", description="Specify required reviewers if specified path is "
			+ "changed. Note that the user submitting the change is considered to reviewed the change automatically")
	@io.onedev.server.annotation.ReviewRequirement
	public String getReviewRequirement() {
		return reviewRequirement;
	}

	public void setReviewRequirement(String reviewRequirement) {
		this.reviewRequirement = reviewRequirement;
	}
	
	public ReviewRequirement getParsedReviewRequirement() {
		if (parsedReviewRequirement == null)
			parsedReviewRequirement = ReviewRequirement.parse(reviewRequirement);
		return parsedReviewRequirement;
	}
	
	public void setParsedReviewRequirement(ReviewRequirement parsedReviewRequirement) {
		this.parsedReviewRequirement = parsedReviewRequirement;
		reviewRequirement = parsedReviewRequirement.toString();
	}
	
	@Editable(order=500, name="Required Builds", placeholder="No any", description="Optionally choose required builds. You may also " +
			"input jobs not listed here, and press ENTER to add them")
	@JobChoice(tagsMode=true)
	public List<String> getJobNames() {
		return jobNames;
	}

	public void setJobNames(List<String> jobNames) {
		this.jobNames = jobNames;
	}

	@Override
	public boolean isValid(ConstraintValidatorContext context) {
		if (getJobNames().isEmpty() && getReviewRequirement() == null) {
			context.disableDefaultConstraintViolation();
			String message = "Either reviewer or required builds should be specified";
			context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
			return false;
		} else {
			return true;
		}
	}
	
}
