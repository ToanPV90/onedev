package io.onedev.server.buildspec.job.action.condition;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import io.onedev.server.model.Build;
import io.onedev.server.util.ProjectScope;
import io.onedev.server.util.criteria.Criteria;

public class PreviousIsFailedCriteria extends Criteria<Build> {

	private static final long serialVersionUID = 1L;

	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<Build, Build> from, CriteriaBuilder builder) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean matches(Build build) {
		return build.getStreamPrevious(null) != null 
				&& build.getStreamPrevious(null).getStatus() == Build.Status.FAILED;
	}

	@Override
	public String toStringWithoutParens() {
		return ActionCondition.getRuleName(ActionConditionLexer.PreviousIsFailed);
	}
	
}
