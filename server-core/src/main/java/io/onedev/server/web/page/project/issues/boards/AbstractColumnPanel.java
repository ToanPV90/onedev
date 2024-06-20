package io.onedev.server.web.page.project.issues.boards;

import io.onedev.commons.utils.ExplicitException;
import io.onedev.server.OneDev;
import io.onedev.server.entitymanager.IssueChangeManager;
import io.onedev.server.entitymanager.IssueManager;
import io.onedev.server.entitymanager.IterationManager;
import io.onedev.server.entitymanager.SettingManager;
import io.onedev.server.model.Issue;
import io.onedev.server.model.Project;
import io.onedev.server.model.support.administration.GlobalIssueSetting;
import io.onedev.server.search.entity.issue.IssueQuery;
import io.onedev.server.security.SecurityUtils;
import io.onedev.server.util.EditContext;
import io.onedev.server.util.ProjectScope;
import io.onedev.server.web.component.floating.FloatingPanel;
import io.onedev.server.web.component.menu.MenuItem;
import io.onedev.server.web.component.menu.MenuLink;
import io.onedev.server.web.page.base.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.visit.IVisitor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
abstract class AbstractColumnPanel extends Panel implements EditContext {

	protected final IModel<Integer> countModel = new LoadableDetachableModel<>() {

		@Override
		protected Integer load() {
			if (getQuery() != null) {
				try {
					return getIssueManager().count(getProjectScope(), getQuery().getCriteria());
				} catch (ExplicitException ignored) {
				}
			}
			return 0;
		}

	};
	
	public AbstractColumnPanel(String id) {
		super(id);
	}

	@Override
	protected void onDetach() {
		countModel.detach();
		super.onDetach();
	}
	
	protected void onCardAdded(AjaxRequestTarget target, Issue issue) {
		findParent(RepeatingView.class).visitChildren(AbstractColumnPanel.class, (IVisitor<AbstractColumnPanel, Void>) (columnPanel, visit) -> {
			if (columnPanel.getQuery() != null && columnPanel.getQuery().matches(issue)) {
				var cardListPanel = columnPanel.getCardListPanel();
				var firstCard = cardListPanel.findCard(null);
				if (firstCard != null)
					issue.setBoardPosition(getIssueManager().load(firstCard.getIssueId()).getBoardPosition() - 1);
				getIssueManager().open(issue);
				cardListPanel.onCardAdded(target, issue.getId());
				visit.stop();
			}
		});		
	}
	
	protected abstract IssueQuery getQuery();
	
	protected abstract CardListPanel getCardListPanel();

	protected GlobalIssueSetting getIssueSetting() {
		return OneDev.getInstance(SettingManager.class).getIssueSetting();
	}
	
	protected IssueChangeManager getIssueChangeManager() {
		return OneDev.getInstance(IssueChangeManager.class);
	}
	
	protected IssueManager getIssueManager() {
		return OneDev.getInstance(IssueManager.class);
	}

	protected IterationManager getIterationManager() {
		return OneDev.getInstance(IterationManager.class);
	}
	
	@Override
	public Object getInputValue(String name) {
		return null;
	}

	protected Project getProject() {
		return getProjectScope().getProject();
	}
	
	protected abstract ProjectScope getProjectScope();

	protected abstract IterationSelection getIterationSelection();
	
	@Nullable
	protected abstract String getIterationPrefix();
	
	protected MenuLink newAddToIterationLink(String componentId) {
		return new MenuLink(componentId) {

			@Override
			protected List<MenuItem> getMenuItems(FloatingPanel dropdown) {
				var menuItems = new ArrayList<MenuItem>();
				for (var iteration: getProject().getSortedHierarchyIterations()) {
					if ((getIterationPrefix() == null || iteration.getName().startsWith(getIterationPrefix()))
							&& (isBacklog() || !iteration.equals(getIterationSelection().getIteration()))) {
						var iterationId = iteration.getId();
						menuItems.add(new MenuItem() {
							@Override
							public String getLabel() {
								return iteration.getName();
							}
	
							@Override
							public WebMarkupContainer newLink(String id) {
								return new AjaxLink<Void>(id) {
	
									@Override
									public void onClick(AjaxRequestTarget target) {
										BasePage page = (BasePage) getPage();
										dropdown.close();
										var iteration = getIterationManager().load(iterationId);
										var issues = new ArrayList<Issue>();
										for (var issue: getIssueManager().query(getProjectScope(), getQuery(),
												false, 0, Integer.MAX_VALUE)) {
											if (issue.getSchedules().stream().noneMatch(it->it.getIteration().equals(iteration)))
												issues.add(issue);
										}
										getIssueChangeManager().addSchedule(issues, iteration);
										for (var issue: issues)
											page.notifyObservablesChange(target, issue.getChangeObservables(true));
									}
	
								};
							}
						});
					}
				}
				return menuItems;
			}
	
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(getQuery() != null && SecurityUtils.canScheduleIssues(getProject()));
			}
	
		};
	}
	
	protected abstract boolean isBacklog();
}
