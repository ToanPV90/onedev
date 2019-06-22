package io.onedev.server.git.command;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import io.onedev.commons.utils.command.Commandline;
import io.onedev.commons.utils.command.LineConsumer;

public class FetchCommand extends GitCommand<Void> {

	private static final Logger logger = LoggerFactory.getLogger(FetchCommand.class);
	
    private String from;
    
    private int depth;
    
    private String[] refspec = new String[0];
    
	public FetchCommand(final File gitDir) {
		super(gitDir);
	}

	public FetchCommand from(String from) {
	    this.from = from;
	    return this;
	}
	
	public FetchCommand refspec(String... refspec) {
		this.refspec = refspec;
		return this;
	}

	public FetchCommand depth(int depth) {
		this.depth = depth;
		return this;
	}
	
	@Override
	public Void call(Logger logger) {
	    Preconditions.checkNotNull(from, "from param has to be specified.");
	    
		Commandline cmd = cmd().addArgs("fetch");
		cmd.addArgs(from);
		cmd.addArgs("--force", "--quiet");
		if (depth != 0)
			cmd.addArgs("--depth", String.valueOf(depth));
		
		for (String each: refspec)
			cmd.addArgs(each);
		
		Logger effectiveLogger = logger!=null?logger:FetchCommand.logger;
		cmd.execute(new LineConsumer() {

			@Override
			public void consume(String line) {
				effectiveLogger.trace(line);
			}
			
		}, new LineConsumer() {

			@Override
			public void consume(String line) {
				effectiveLogger.error(line);
			}
			
		}, logger).checkReturnCode();
		
		return null;
	}

}