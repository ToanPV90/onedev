package io.onedev.server.web.translation;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.wicket.Localizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.onedev.commons.bootstrap.Bootstrap;

public class Translation extends TranslationResourceBundle {

	private static final Logger logger = LoggerFactory.getLogger(Translation.class);

	public static String _T(String key) {
		return Localizer.get().getString("t: " + key, null);
	}
	
	public static void watchUpdate(Class<? extends TranslationResourceBundle> translationClass, Runnable onUpdate) {
		if (Bootstrap.sandboxMode && !Bootstrap.prodMode) {
			var classResource = translationClass.getResource(translationClass.getSimpleName() + ".class");
			if (classResource != null) {
				try {
					var classFile = new File(classResource.toURI());
					var lastModifiedRef = new AtomicLong(classFile.lastModified());
					var stop = new AtomicBoolean(false);
					new Thread(() -> {
						while (!stop.get()) {
							try {
								Thread.sleep(100);
								var lastModified = classFile.lastModified();
								if (lastModified != lastModifiedRef.get()) {
									lastModifiedRef.set(lastModified);
									Thread.sleep(2000);
									onUpdate.run();
								}
							} catch (InterruptedException e) {
							}
						}
					}).start();
					
					Runtime.getRuntime().addShutdownHook(new Thread(() -> {
						stop.set(true);
					}));
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}
			} else {
				logger.error("Unable to locate class file for: {}", translationClass.getName());
			}
		}
	}


	@Override
	protected Map<String, String> getContents() {
		return Collections.emptyMap();
	}

}
