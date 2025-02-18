package io.onedev.server.web.page.project.pullrequests.detail.activities.activity;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.jetbrains.annotations.Nullable;

import io.onedev.server.OneDev;
import io.onedev.server.attachment.AttachmentSupport;
import io.onedev.server.attachment.ProjectAttachmentSupport;
import io.onedev.server.entitymanager.PullRequestChangeManager;
import io.onedev.server.entitymanager.PullRequestReactionManager;
import io.onedev.server.model.Project;
import io.onedev.server.model.PullRequest;
import io.onedev.server.model.User;
import io.onedev.server.model.support.EntityReaction;
import io.onedev.server.security.SecurityUtils;
import io.onedev.server.util.DateUtils;
import io.onedev.server.web.component.comment.CommentPanel;
import io.onedev.server.web.component.markdown.ContentVersionSupport;
import io.onedev.server.web.page.base.BasePage;
import io.onedev.server.web.util.DeleteCallback;

class PullRequestOpenedPanel extends GenericPanel<PullRequest> {

	public PullRequestOpenedPanel(String id, IModel<PullRequest> model) {
		super(id, model);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		PullRequest request = getPullRequest();
		add(new Label("user", request.getSubmitter().getDisplayName()));
		add(new Label("age", DateUtils.formatAge(request.getSubmitDate()))
			.add(new AttributeAppender("title", DateUtils.formatDateTime(request.getSubmitDate()))));
		
		add(new CommentPanel("body") {

			@Override
			protected String getComment() {
				return getPullRequest().getDescription();
			}

			@Override
			protected void onSaveComment(AjaxRequestTarget target, String comment) {
				OneDev.getInstance(PullRequestChangeManager.class).changeDescription(getPullRequest(), comment);
				((BasePage)getPage()).notifyObservableChange(target,
						PullRequest.getChangeObservable(getPullRequest().getId()));
			}

			@Nullable
			@Override
			protected String getAutosaveKey() {
				return "pull-request:" + getPullRequest().getId() + ":description"; 
			}

			@Override
			protected Project getProject() {
				return getPullRequest().getTargetProject();
			}

			@Override
			protected List<User> getParticipants() {
				return getPullRequest().getParticipants();
			}
			
			@Override
			protected AttachmentSupport getAttachmentSupport() {
				return new ProjectAttachmentSupport(getProject(), getPullRequest().getUUID(), 
						SecurityUtils.canManagePullRequests(getProject()));
			}

			@Override
			protected boolean canManageComment() {
				return SecurityUtils.canModifyPullRequest(getPullRequest());
			}

			@Override
			protected String getRequiredLabel() {
				return null;
			}

			@Override
			protected String getEmptyDescription() {
				return "No description";
			}
			
			@Override
			protected ContentVersionSupport getContentVersionSupport() {
				return new ContentVersionSupport() {

					@Override
					public long getVersion() {
						return 0;
					}
					
				};
			}

			@Override
			protected DeleteCallback getDeleteCallback() {
				return null;
			}

			@Override
			protected Collection<? extends EntityReaction> getReactions() {
				return getPullRequest().getReactions();
			}

			@Override
			protected void onToggleEmoji(AjaxRequestTarget target, String emoji) {
				OneDev.getInstance(PullRequestReactionManager.class).toggleEmoji(
						SecurityUtils.getUser(), 
						getPullRequest(), 
						emoji);
			}
			
		});
	}

	private PullRequest getPullRequest() {
		return getModelObject();
	}
	
}
