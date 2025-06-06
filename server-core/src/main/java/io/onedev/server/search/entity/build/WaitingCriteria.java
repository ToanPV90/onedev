package io.onedev.server.search.entity.build;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import io.onedev.server.model.Build;
import io.onedev.server.util.ProjectScope;

public class WaitingCriteria extends StatusCriteria {

	private static final long serialVersionUID = 1L;

	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<Build, Build> from, CriteriaBuilder builder) {
		Path<?> attribute = from.get(Build.PROP_STATUS);
		return builder.equal(attribute, Build.Status.WAITING);
	}

	@Override
	public boolean matches(Build build) {
		return build.getStatus() == Build.Status.WAITING;
	}

	@Override
	public String toStringWithoutParens() {
		return BuildQuery.getRuleName(BuildQueryLexer.Waiting);
	}

	@Override
	public Build.Status getStatus() {
		return Build.Status.WAITING;
	}

}
