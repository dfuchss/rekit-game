package rekit.util;

import java.util.function.Consumer;
import java.util.function.Function;

import rekit.config.GameConf;

public class LambdaTools {
	private LambdaTools() {
	}

	@FunctionalInterface
	public interface RunnableWithException {
		void apply() throws Exception;
	}

	@FunctionalInterface
	public interface FunctionWithException<I, O> {
		O apply(I i) throws Exception;
	}

	@FunctionalInterface
	public interface ConsumerWithException<I> {
		void apply(I i) throws Exception;
	}

	public static final <I, O> Function<I, O> tryCatch(FunctionWithException<I, O> in) {
		return i -> {
			try {
				return in.apply(i);
			} catch (Exception e) {
				GameConf.GAME_LOGGER.error(e.getMessage());
				return null;
			}
		};
	}

	public static final Runnable tryCatch(RunnableWithException in) {
		return () -> {
			try {
				in.apply();
			} catch (Exception e) {
				GameConf.GAME_LOGGER.error(e.getMessage());
			}
		};

	}

	public static final <I> Consumer<I> tryCatch(ConsumerWithException<I> in) {
		return i -> {
			try {
				in.apply(i);
			} catch (Exception e) {
				GameConf.GAME_LOGGER.error(e.getMessage());
			}
		};

	}

}