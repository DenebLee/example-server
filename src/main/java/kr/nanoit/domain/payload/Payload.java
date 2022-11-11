package kr.nanoit.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JSON VALUE TYPE
 * - NUMBER
 * - DOUBLE
 * - BOOLEAN
 * - STRING
 * <p>
 * - ARRAY
 * - OBJECT
 * <p>
 * {
 * "type": "AUTHENTICATION",
 * "uuid": "123421341234",
 * "data": {
 * "username": "ppzxc",
 * "password": "ppppp"
 * }
 * }
 * <p>
 * {
 * "type": "SEND",
 * "uuid": "123421341234",
 * "data": {
 * "id": 1234,
 * "phone": "01011112222",
 * "callback": "15449405",
 * "content": "안녕"
 * }
 * }
 * <p>
 * * {
 * *    "type": "SEND",
 *      "messageUuid": "123421341234",
 * *    "data": {
 * *        "id": 1234,
 * *        "phone": "01011112222",
 * *        "callback": "15449405",
 * *        "content": "안녕"
 * *    }
 * * }
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payload {
    private PayloadType type;
    private String messageUuid;
    private Object data;
}
