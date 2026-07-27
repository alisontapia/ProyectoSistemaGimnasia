package edu.unl.ec.gimnasia.domain.security;

/**
 * Define los tipos de acciones o permisos que pueden
 * asignarse a un usuario sobre un recurso del sistema.
 *
 * READ   : Permite únicamente la consulta de información.
 * UPDATE : Permite modificar la información existente.
 * ALL    : Otorga acceso completo sobre el recurso.
 */

public enum ActionType {
    READ,
    UPDATE,
    ALL
}
