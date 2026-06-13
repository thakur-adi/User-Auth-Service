package dev.aditya.userauthservice.Model;


public class RoleFactory {

    public static Role createRole(String roleName)
    {
        Role role = new Role();
        switch (roleName.toLowerCase())
        {
            case "admin":
                role.setRoleName(RoleName.ADMIN);
            case "super admin":
                role.setRoleName(RoleName.SUPER_ADMIN);
            case "owner":
                role.setRoleName(RoleName.OWNER);
            default:
                role.setRoleName(RoleName.USER);
        }
        return role;
    }
}
