package com.pedestriamc.strings.paper.user;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.common.user.UserBuilder;
import com.pedestriamc.strings.paper.platform.PaperStringsUser;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Function;

public class PaperUserBuilder {

    private final UserBuilder<PaperStringsUser> builder;

    public PaperUserBuilder(@NotNull StringsPlatform strings, @NotNull UUID uuid, boolean isNew) {
        Function<UserBuilder<PaperStringsUser>, PaperStringsUser> buildFunction = b -> new PaperStringsUser(b);
        this.builder = new UserBuilder<>(buildFunction, strings, uuid, isNew);
    }

    public UserBuilder<PaperStringsUser> getBuilder() {
        return builder;
    }
}
