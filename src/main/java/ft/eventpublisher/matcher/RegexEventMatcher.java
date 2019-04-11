package ft.eventpublisher.matcher;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class RegexEventMatcher extends AbstractEventMatcher {
    private final Set<Pattern> patternList = new HashSet<>();
    private Class<?> evenType;

    @Override
    public void initialize(final Class<?> eventType, final String[] params) {
        this.evenType = eventType;
        Stream.of(params)
                .forEach(param -> patternList.add(Pattern.compile(param)));
    }

    @Override
    public Class<?> getEventType() {
        return evenType;
    }

    @Override
    protected boolean matchesImpl(final Object event) {
        String stringRepresenation = event.toString();
        return patternList.stream()
                .anyMatch(pattern -> pattern.matcher(stringRepresenation).matches());
    }
}
