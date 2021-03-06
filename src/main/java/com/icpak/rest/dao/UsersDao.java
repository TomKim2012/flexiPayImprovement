package com.icpak.rest.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.shiro.crypto.hash.Sha256Hash;

import com.google.inject.Inject;
import com.icpak.rest.exceptions.ServiceException;
import com.icpak.rest.models.ErrorCodes;
import com.icpak.rest.models.auth.Role;
import com.icpak.rest.models.auth.User;
import com.icpak.rest.models.membership.Member;
import com.icpak.rest.models.membership.MemberImport;
import com.icpak.rest.models.util.Attachment;
import com.workpoint.icpak.shared.model.UserDto;

public class UsersDao extends BaseDao {
	Logger logger = Logger.getLogger(UsersDao.class);

	@Inject
	RolesDao rolesDao;

	public User findUserByEmail(String email) {
		assert email != null;
		String query = "from User u where u.email = :email";
		return getSingleResultOrNull(getEntityManager().createQuery(query).setParameter("email", email));
	}

	public User findUserByUsername(String username) {
		assert username != null;
		String query = "from User u where u.username = :username";
		return getSingleResultOrNull(getEntityManager().createQuery(query).setParameter("username", username));
	}

	public User findUserByMemberNo(String memberNo) {
		assert memberNo != null;
		logger.info(" =====Searching user with memberNo ============" + memberNo);
		String query = "from User u where u.memberNo =:memberNo";
		return getSingleResultOrNull(getEntityManager().createQuery(query).setParameter("memberNo", memberNo));
	}

	public User getUserByUsernameOrMemberNo(String username) {
		User user = findUserByMemberNo(username);
		if (user == null) {
			user = findUserByUsername(username);
		}

		if (user != null) {
			if (user.getRoles().isEmpty()) {
				createDefaultRoles();
				Role role = rolesDao.getByName("MEMBER");

				if (role != null) {
					user.addRole(role);

					return (User) save(user);
				}
			}
		}
		return user;
	}

	public User getByUserPhoneNo(String userPhoneNo, boolean throwExceptionIfNull) {
		User user = getSingleResultOrNull(
				getEntityManager().createQuery("from User u where u.phoneNumber like :userPhoneNo")
						.setParameter("userPhoneNo", "%" + userPhoneNo + "%"));

		if (throwExceptionIfNull && user == null) {
			throw new ServiceException(ErrorCodes.NOTFOUND, "PhoneNo.", "'" + userPhoneNo + "'");
		}

		return user;
	}

	public User createUser(User user) {
		if (user.getHashedPassword() != null && user.getId() == null) {
			user.setPassword(encrypt(user.getHashedPassword()));
		}

		// Update QrCode String to MemberRefId when QRString is null
		if (user.getMember() != null) {
			if (user.getMember().getMemberQrCode() == null || user.getMember().getMemberQrCode().isEmpty()) {
				Member m = user.getMember();
				m.setMemberQrCode(user.getRefId());
			}
		}
		return (User) save(user);
	}

	public void changePassword(User user) {
		user.setPassword(encrypt(user.getHashedPassword()));
		createUser(user);
	}

	public String encrypt(String password) {
		return new Sha256Hash(password).toHex();
	}

	public List<User> getAllUsers(Integer offSet, Integer limit, Role role, String searchTerm) {
		if (role == null) {
			String query = "from User u where isActive=1 and " + "(u.memberNo like :searchTerm or "
					+ "u.userData.fullNames like :searchTerm or " + "u.email like :searchTerm or "
					+ "u.memberNo like :searchTerm" + ")" + "order by id";
			return getResultList(
					getEntityManager().createQuery(query).setParameter("searchTerm", "%" + searchTerm + "%"), offSet,
					limit);
		}

		return getResultList(getEntityManager().createQuery("select u from User u" + " inner join u.roles roles "
				+ " where roles=:role " + " and u.isActive=1" + " order by username").setParameter("role", role),
				offSet, limit);
	}

	public List<UserDto> getAllUsersDtos(Integer offSet, Integer limit, Role role, String searchTerm) {
		if (role == null) {
			String query = "select memberNo,firstName,lastName,fullName,email,lmsStatus from User u where isActive=1 and "
					+ "(u.memberNo like :searchTerm or " + "u.fullName like :searchTerm or "
					+ "u.email like :searchTerm or " + "u.memberNo like :searchTerm" + ")" + "order by id";

			List<Object[]> rows = getResultList(
					getEntityManager().createNativeQuery(query).setParameter("searchTerm", "%" + searchTerm + "%"),
					offSet, limit);

			List<UserDto> userDtos = new ArrayList<>();
			for (Object[] row : rows) {
				int i = 0;
				Object value = null;
				String memberNo = (value = row[i++]) == null ? null : value.toString().trim();
				String firstName = (value = row[i++]) == null ? null : value.toString().trim();
				String lastName = (value = row[i++]) == null ? null : value.toString().trim();
				String fullName = (value = row[i++]) == null ? null : value.toString().trim();
				String email = (value = row[i++]) == null ? null : value.toString().trim();
				String lmsStatus = (value = row[i++]) == null ? null : value.toString().trim();
				UserDto user = new UserDto();
				user.setMemberNo(memberNo);
				if (fullName == null) {
					fullName = firstName + " " + lastName;
				}
				user.setFullName(fullName);
				user.setEmail(email);
				userDtos.add(user);
			}

			return userDtos;
		}

		return getResultList(getEntityManager().createQuery("select u from User u" + " inner join u.roles roles "
				+ " where roles=:role " + " and u.isActive=1" + " order by username").setParameter("role", role),
				offSet, limit);
	}

	public List<User> getAllUsers() {
		String query = "from User u where memberNo IS NOT NULL order by memberNo asc";
		return getResultList(getEntityManager().createQuery(query));
	}

	public void updateUser(User user) {
		createUser(user);
	}

	public int getUserCount(String searchTerm) {
		return getUserCount(null, searchTerm);
	}

	public int getUserCount(String roleId, String searchTerm) {
		Number number = null;
		if (roleId == null) {
			number = getSingleResultOrNull(getEntityManager()
					.createNativeQuery("select count(*) from user where isactive=1 and "
							+ "(Name like :searchTerm or email like :searchTerm " + " or memberNo like :searchTerm)")
					.setParameter("searchTerm", "%" + searchTerm + "%"));
		} else {
			number = getSingleResultOrNull(getEntityManager()
					.createNativeQuery("select count(*) from user u " + "inner join user_role ur on (ur.refId=u.id) "
							+ "inner join role r on (ur.roleid=r.id)" + "where u.isactive=1 and u.isactive=1"));
		}

		return number.intValue();
	}

	public User findByUserId(String refId) {
		return findByUserId(refId, true);
	}

	public User findByUserId(String refId, boolean throwExceptionIfNull) {
		User user = getSingleResultOrNull(
				getEntityManager().createQuery("from User u where u.isActive=1 and (u.refId=:refId or u.email=:email)")
						.setParameter("refId", refId).setParameter("email", refId));

		if (user == null && throwExceptionIfNull) {
			throw new ServiceException(ErrorCodes.NOTFOUND, "User", "'" + refId + "'");
		}
		return user;
	}

	public User findByUserActivationEmail(String email, boolean throwExceptionIfNull) {
		User user = getSingleResultOrNull(getEntityManager()
				.createQuery("from User u where u.isActive=1 and (u.email like :email1 or u.email like :email2)")
				.setParameter("email1", email + "%").setParameter("email2", "%" + email));

		if (user == null && throwExceptionIfNull) {
			throw new ServiceException(ErrorCodes.NOTFOUND, "User", "'" + email + "'");

		}
		return user;
	}

	public List<User> findByUserActivationEmail(String email) {
		List<User> users = getResultList(getEntityManager()
				.createQuery("from User u where u.isActive=1 and " + "(u.email like :email1 or u.email like :email2)")
				.setParameter("email1", email + "%").setParameter("email2", "%" + email));
		return users;
	}

	public List<User> findByUserActivationEmailExclusive(String email) {
		List<User> users = getResultList(
				getEntityManager().createQuery("from User u where u.isActive=1 and " + "u.email = :email1")
						.setParameter("email1", email));
		return users;
	}

	public int disableProfilePics(String userId) {
		User user = findByUserId(userId);

		String update = "UPDATE Attachment set isActive=0 where user=:user";
		int rows = getEntityManager().createQuery(update).setParameter("user", user).executeUpdate();

		return rows;
	}

	public Attachment getProfilePic(String userId) {

		String sql = "SELECT a FROM Attachment a where a.isActive=1 and a.profilePicUserId=:userid";

		Attachment attachment = getSingleResultOrNull(
				getEntityManager().createQuery(sql).setParameter("userid", userId));
		if (attachment == null) {
			throw new ServiceException(ErrorCodes.NOTFOUND, "Profile Picture", " for user " + userId);
		}
		return attachment;
	}

	public String getApplicationRefId(String userRef) {
		String sql = "select refid from `Application Form Header` where userRefId=:userRef";
		return getSingleResultOrNull(getEntityManager().createNativeQuery(sql).setParameter("userRef", userRef));
	}

	public boolean hasMember(User po) {

		Number val = getSingleResultOrNull(
				getEntityManager().createQuery("select count(*) from Member where userRefId=:userId")
						.setParameter("userId", po.getRefId()));

		return val.intValue() > 0;
	}

	public String getMemberRefId(Long userId) {

		String sql = "select m.refId from member m where m.userId=:id";

		String memberRefId = getSingleResultOrNull(
				getEntityManager().createNativeQuery(sql).setParameter("id", userId));

		return memberRefId;
	}

	public String getFullNames(String refId) {
		return getSingleResultOrNull(getEntityManager()
				.createNativeQuery("select u.fullName from user u "
						+ "inner join Member m on m.userRefId=u.refId where m.refId=:refId")
				.setParameter("refId", refId));
	}

	public String getNamesBymemberNo(String memberNo) {
		return getSingleResultOrNull(getEntityManager()
				.createNativeQuery("select u.fullName from user u "
						+ "inner join Member m on m.userRefId=u.refId where u.memberNo=:memberNo")
				.setParameter("memberNo", memberNo));
	}

	public void deleteAllRolesForCurrentUser(Long passedId) {
		Query query = (getEntityManager().createNativeQuery("delete from user_role where userid=:passedUserId")
				.setParameter("passedUserId", passedId));
		query.executeUpdate();
	}

	public String getGender(String applicationRefId) {
		return getSingleResultOrNull(getEntityManager()
				.createNativeQuery("select a.gender from `Application Form Header` a where a.refId=:refId")
				.setParameter("refId", applicationRefId));
	}

	public void createDefaultRoles() {
		logger.info(" CREATING DEFAULT ROLES ======= ");
		List<Role> roles = new ArrayList<>();

		Role role = new Role();
		role.setName("MEMBER");
		role.setRoleId(new Long(3));
		role.setUserTypeID(new Long(26));
		role.setDescription(" Role for members only");
		roles.add(role);

		role = new Role();
		role.setName("TRAINER");
		role.setRoleId(new Long(2));
		role.setUserTypeID(new Long(25));
		role.setDescription(" Role for trainers only");
		roles.add(role);

		role = new Role();
		role.setName("ADMIN");
		role.setRoleId(new Long(1));
		role.setUserTypeID(new Long(25));
		role.setDescription(" Role for Administrator only");
		roles.add(role);

		for (Role r : roles) {
			Role roleInDb = rolesDao.getByName(r.getName());

			if (roleInDb == null) {
				rolesDao.createRole(r);
			}
		}
	}

	public List<User> findUsersByMemberNo(String memberNo) {
		assert memberNo != null;
		String query = "from User u where u.memberNo = :memberNo";
		return getResultList((getEntityManager().createQuery(query).setParameter("memberNo", memberNo)));
	}

	public List<MemberImport> importUsers() {
		List<MemberImport> memberImports = new ArrayList<>();
		String sql = "select Name,phoneNo_ from karagita a";
		Query query = getEntityManager().createNativeQuery(sql);

		List<Object[]> rows = getResultList(query);

		for (Object[] row : rows) {
			int i = 0;
			Object value = null;
			MemberImport memberImport = new MemberImport();
			memberImport.setName((value = row[i++]) == null ? null : value.toString());
			memberImport.setPhoneNo_((value = row[i++]) == null ? null : value.toString());
			memberImports.add(memberImport);
		}
		return memberImports;
	}
}