-- Given a table Users and Roles, provide the SQL statement that get the list of all Users that
-- don't have an specific Role ABC but belong to a Group XYZ.

-- Not assuming anything

SELECT * FROM public.acl_user AS user1
WHERE user1.user_id NOT IN (
	SELECT user_id FROM public.acl_user_role user_role WHERE user_role.role_id IN (
		SELECT role_id FROM public.acl_role AS role1 WHERE role1.role_name = 'AAA'
	)
) AND user1.user_id IN (
	SELECT user_id FROM public.acl_user_group user_group WHERE user_group.group_id IN (
		SELECT group_id FROM public.acl_group AS group1 WHERE group1.group_name = 'XYZ'
	)
);

SELECT * FROM public.acl_user AS user1
WHERE user1.user_id NOT IN (
	SELECT user_id FROM public.acl_user_role AS user_role
	INNER JOIN public.acl_role AS role1 ON user_role.role_id = role1.role_id
	WHERE role1.role_name <> 'AAA'
) AND user1.user_id IN (
	SELECT user_id FROM public.acl_user_group AS user_group
	INNER JOIN public.acl_group AS group1 ON user_group.group_id = group1.group_id
	WHERE group1.group_name = 'XYZ'
);

-- Assuming Role ABC has role_id 1 Group XYZ has group_id 9

SELECT * FROM public.acl_user AS user1
WHERE user1.user_id NOT IN (SELECT user_id FROM public.acl_user_role user_role WHERE user_role.role_id = 1)
AND user1.user_id IN (SELECT user_id FROM public.acl_user_group user_group WHERE user_group.group_id = 9);
