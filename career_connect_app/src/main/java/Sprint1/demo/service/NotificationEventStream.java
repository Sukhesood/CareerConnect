package Sprint1.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/** Manages authenticated, browser-side Server-Sent Event connections per recipient. */
@Service
public class NotificationEventStream {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationEventStream.class);
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> listeners = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String email) {
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(20));
        listeners.computeIfAbsent(email, key -> new CopyOnWriteArrayList<>()).add(emitter);
        Runnable cleanup = () -> remove(email, emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(error -> cleanup.run());
        try {
            emitter.send(SseEmitter.event().name("connected").data("ready", MediaType.TEXT_PLAIN));
        } catch (IOException | IllegalStateException ex) {
            cleanup.run();
            LOG.debug("SSE connection closed for {}", email, ex);
        }
        return emitter;
    }

    public void published(String email, Long notificationId) {
        for (SseEmitter emitter : listeners.getOrDefault(email, new CopyOnWriteArrayList<>())) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .id(Long.toString(notificationId))
                        .data(Long.toString(notificationId), MediaType.TEXT_PLAIN));
            } catch (IOException | IllegalStateException ex) {
                remove(email, emitter);
                LOG.debug("Removed disconnected SSE client for {}", email);
            }
        }
    }

    // Keep connections healthy and discover clients that have disconnected.
    @Scheduled(fixedDelay = 30000)
    public void heartbeat() {
        listeners.forEach((email, emitters) -> {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().comment("heartbeat"));
                } catch (IOException | IllegalStateException ex) {
                    remove(email, emitter);
                }
            }
        });
    }

    private void remove(String email, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> clients = listeners.get(email);
        if (clients != null) {
            clients.remove(emitter);
            if (clients.isEmpty()) listeners.remove(email, clients);
        }
    }
}