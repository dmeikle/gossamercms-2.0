package com.gossamercms.users.adapters.inbound;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.mvc.http.ApiResponse;
import com.gossamercms.mvc.http.ResponseBuilder;
import com.gossamercms.users.api.*;
import com.gossamercms.users.api.UserDirectoryDto;
import com.gossamercms.users.api.responses.UserDetailsResponse;
import com.gossamercms.users.handlers.UserAddressesHandler;
import com.gossamercms.users.handlers.UserContextsHandler;
import com.gossamercms.users.handlers.UserTelephonesHandler;
import com.gossamercms.users.handlers.UsersHandler;
import com.gossamercms.users.domain.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;


@RestController
@RequestMapping("/admin/users")
public class AdminUsersController extends BaseController<User, UserDto> {

    private  UserTelephonesHandler telephonesHandler;
    private UserAddressesHandler userAddressesHandler;
    private UserContextsHandler userContextsHandler;

    public AdminUsersController(UsersHandler handler,
                                UserTelephonesHandler telephonesHandler,
                                UserAddressesHandler userAddressesHandler,
                                UserContextsHandler userContextsHandler) {
        super(handler);
        this.telephonesHandler = telephonesHandler;
        this.userAddressesHandler = userAddressesHandler;
        this.userContextsHandler = userContextsHandler;
    }

    @GetMapping("/list-all")
    public List<UserDto> listAllRaw() {
        QueryOptions options = QueryOptions.of(1, -1, Map.of(), Map.of());
        return handler.getAll(options).list();
    }
    @GetMapping("/directory")
    public ApiResponse<ListResultset<UserDirectoryDto>> directory(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "50") int size,
            @RequestParam Map<String, Object> params
    ) {

        Object sort = params.remove("sort");
        Object dir = params.remove("dir");

        params.remove("page");
        params.remove("size");

        Map<String, String> orderBy = Map.of();

        if (sort != null) {
            orderBy = Map.of(
                    sort.toString(),
                    dir == null ? "ASC" : dir.toString().toUpperCase()
            );
        }

        QueryOptions options = QueryOptions.of(
                page,
                size,
                params,
                orderBy
        );

        ListResultset<UserDirectoryDto> directory =
                ((UsersHandler) handler).directory(options);

        return ResponseBuilder.ok(directory);
    }

    @GetMapping("/{user}/detailed")
    public UserDetailsResponse getDetailed(@PathVariable("user") UserDto user) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<UserDetailDto> userDetailFuture =
                    CompletableFuture.supplyAsync(
                            () -> ((UsersHandler) handler).getUserDetail(user.getId()),
                            executor
                    );

            CompletableFuture<UserTelephoneDto> telephoneFuture =
                    CompletableFuture.supplyAsync(
                            () -> telephonesHandler.get(Map.of("userId", user.getId())),
                            executor
                    );

            CompletableFuture<List<AddressDto>> addressesFuture =
                    CompletableFuture.supplyAsync(
                            () -> userAddressesHandler.getAll(
                                    QueryOptions.builder()
                                            .page(1)
                                            .filters(Map.of("userId", user.getId()))
                                            .build()
                            ).list(),
                            executor
                    );

            CompletableFuture<List<UserContextDetailDto>> contextsFuture =
                    CompletableFuture.supplyAsync(
                            () -> userContextsHandler.getDetailed(
                                    QueryOptions.builder()
                                            .page(1)
                                            .filters(Map.of("userId", user.getId()))
                                            .build()
                            ).list(),
                            executor
                    );

            CompletableFuture.allOf(
                    telephoneFuture,
                    addressesFuture,
                    contextsFuture
            ).join();

            return new UserDetailsResponse(
                    userDetailFuture.join(),
                    telephoneFuture.join(),
                    addressesFuture.join(),
                    contextsFuture.join()
            );
        }
    }

}


