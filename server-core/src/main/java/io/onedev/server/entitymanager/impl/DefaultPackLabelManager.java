package io.onedev.server.entitymanager.impl;

import com.google.common.base.Preconditions;
import io.onedev.server.entitymanager.LabelSpecManager;
import io.onedev.server.entitymanager.PackLabelManager;
import io.onedev.server.model.AbstractEntity;
import io.onedev.server.model.LabelSpec;
import io.onedev.server.model.Pack;
import io.onedev.server.model.PackLabel;
import io.onedev.server.persistence.annotation.Sessional;
import io.onedev.server.persistence.dao.Dao;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;

@Singleton
public class DefaultPackLabelManager extends BaseEntityLabelManager<PackLabel> implements PackLabelManager {

	@Inject
    public DefaultPackLabelManager(Dao dao, LabelSpecManager labelSpecManager) {
        super(dao, labelSpecManager);
    }

	@Override
	protected PackLabel newEntityLabel(AbstractEntity entity, LabelSpec spec) {
		var label = new PackLabel();
		label.setPack((Pack) entity);
		label.setSpec(spec);
		return label;
	}

	@Override
	public void create(PackLabel packLabel) {
		Preconditions.checkState(packLabel.isNew());
		dao.persist(packLabel);
	}

	@Sessional
	@Override
	public void populateLabels(Collection<Pack> packs) {
		var builder = getSession().getCriteriaBuilder();
		CriteriaQuery<PackLabel> labelQuery = builder.createQuery(PackLabel.class);
		Root<PackLabel> labelRoot = labelQuery.from(PackLabel.class);
		labelQuery.select(labelRoot);
		labelQuery.where(labelRoot.get(PackLabel.PROP_PACK).in(packs));

		for (var pack: packs)
			pack.setLabels(new ArrayList<>());

		for (var label: getSession().createQuery(labelQuery).getResultList())
			label.getPack().getLabels().add(label);
	}

}