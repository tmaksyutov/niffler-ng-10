package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.user.FriendshipEntity;
import guru.qa.niffler.data.entity.user.FriendshipStatus;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserSetExtractor implements ResultSetExtractor<UserEntity> {

    public static UserSetExtractor instance = new UserSetExtractor();

    private UserSetExtractor() {
    }

    @Override
    public UserEntity extractData(ResultSet rs) throws SQLException {
        Map<UUID, UserEntity> users = new ConcurrentHashMap<>();
        UserEntity user = null;

        while (rs.next()) {
            UUID userId = rs.getObject("id", UUID.class);
            // Сохраняем значения полей пользователя во внешние переменные
            // чтобы избежать проблем с ResultSet внутри лямбды
            final String username = rs.getString("username");
            final String currency = rs.getString("currency");
            final String firstname = rs.getString("firstname");
            final String surname = rs.getString("surname");
            final String fullname = rs.getString("full_name");
            final byte[] photo = rs.getBytes("photo");
            final byte[] photoSmall = rs.getBytes("photo_small");

            user = users.computeIfAbsent(userId, id -> {
                UserEntity result = new UserEntity();
                result.setId(id);
                result.setUsername(username);
                if (currency != null) {
                    try {
                        result.setCurrency(CurrencyValues.valueOf(currency));
                    } catch (IllegalArgumentException e) {
                        result.setCurrency(CurrencyValues.RUB);
                    }
                }
                result.setFirstname(firstname);
                result.setSurname(surname);
                result.setFullname(fullname);
                result.setPhoto(photo);
                result.setPhotoSmall(photoSmall);

                // Инициализируем списки, чтобы избежать NPE
                result.setFriendshipRequests(new ArrayList<>());
                result.setFriendshipAddressees(new ArrayList<>());

                return result;
            });

            // Получаем данные о дружбе с проверкой на null
            UUID requesterId = rs.getObject("requester_id", UUID.class);
            UUID addresseeId = rs.getObject("addressee_id", UUID.class);
            String statusStr = rs.getString("status");

            // Создаем FriendshipEntity только если есть данные о дружбе
            if (requesterId != null || addresseeId != null || statusStr != null) {
                FriendshipEntity friendship = new FriendshipEntity();

                if (requesterId != null) {
                    UserEntity requester = new UserEntity();
                    requester.setId(requesterId);
                    friendship.setRequester(requester);
                }

                if (addresseeId != null) {
                    UserEntity addressee = new UserEntity();
                    addressee.setId(addresseeId);
                    friendship.setAddressee(addressee);
                }
                // Проверяем status на null перед преобразованием в enum
                if (statusStr != null) {
                    try {
                        FriendshipStatus status = FriendshipStatus.valueOf(statusStr);
                        friendship.setStatus(status);
                    } catch (IllegalArgumentException e) {
                        // Обработка случая, когда значение status не соответствует enum
                        friendship.setStatus(FriendshipStatus.PENDING);
                    }
                }

                if (requesterId != null && requesterId.equals(userId)) {
                    user.getFriendshipRequests().add(friendship);
                }

                if (addresseeId != null && addresseeId.equals(userId)) {
                    user.getFriendshipAddressees().add(friendship);
                }
            }
        }
        return user;
    }
}