import random

for user_id in range(1, 101):
	for _ in range(0, 3):
		group_id = random.randint(1, 9)
		print("INSERT INTO acl_user_group (user_id, group_id) VALUES ({0}, {1});".format(user_id, group_id))
	for _ in range(0, 3):
		role_id = random.randint(1, 10)
		print("INSERT INTO acl_user_role (user_id, role_id) VALUES ({0}, {1});".format(user_id, role_id))
