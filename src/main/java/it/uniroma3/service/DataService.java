package it.uniroma3.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetMediaCommentsRequest;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowingRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.InstagramUserFeedRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramComment;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedItem;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetMediaCommentsResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.logic.InstaConfig;
import it.uniroma3.model.Comment;
import it.uniroma3.model.InstagramUserDB;
import it.uniroma3.model.Media;
import it.uniroma3.model.ProfileSubject;
import it.uniroma3.model.Status;

@Service
public class DataService {

	@Autowired
	private InstaConfig instaconf;

	@Autowired 
	private InstagramUserDBService instaUserDBService;

	@Autowired
	private ProfileSubjectService profileService;

	@Autowired
	private MediaService mediaService;

	@Autowired 
	private CommentService commentService;

	@Autowired 
	private StatusService statusService;

	private Status status;

	private Instagram4j instagram;                                           //api instagram4j


	public void Search(String account) throws Exception {

		//prendo l'istanza singleton della classe instagram delle api per poterla utilizzare
		this.instagram =instaconf.config();

		if(this.profileService.esiste(account)) {

			Status s=this.statusService.cercaPerUsernameSubject(account);

			if(s.getNextFollower().equals("finito") && s.getNextFollowing().equals("finito")) {
				System.out.println("\n[la ricerca è gia completa su questo account]\n");
				return ;
			}

			this.completaRicerca(account);
		}

		else this.primaRicerca(account);

		return ;
	}

	public void primaRicerca(String account) throws Exception{

		//creo il nuovo log da cui partire per completare la ricerca
		this.status=new Status(account);

		//prendo i dati dell'account instagram su cui sto conducendo la ricerca
		InstagramSearchUsernameResult userResult= FetchSubjectData(account);

		//salvo il soggetto della ricerca
		ProfileSubject ps=new ProfileSubject();
		ps.setUsername(userResult.getUser().getUsername());
		this.profileService.inserisci(ps);

		//prendo i dati dei follower dell'utente 
		List<InstagramUserDB> followers= FetchFollowersData(userResult);

		//prendo i dati degli utenti che il soggetto della ricerca segue (following)
		List<InstagramUserDB> following= FetchFollowingData(userResult); 

		//salvo il log (status) per completare la ricerca
		this.statusService.inserisci(status);

		//prendo i dati relativi ai post dell'utente
		ProfileSubject p=this.profileService.cercaPerUsername(userResult.getUser().getUsername());
		List<Media> media=FetchMediaData(p,userResult);


		//salvo i dati del soggetto della ricerca
		InstagramUserDB user=SetSingleUserData(userResult.getUser());

		p.setProfile(user);

		p.setFollowers(followers);

		p.setFollowing(following);

		p.setPosts(media);

		this.profileService.inserisci(p);
		
		System.out.println("la prima parte della ricerca è terminata, completala più tardi\n");
		
		return ;

	}

	public void completaRicerca(String account) throws Exception{

		this.status = this.statusService.cercaPerUsernameSubject(account);

		ProfileSubject ps=this.profileService.cercaPerUsername(account);

		List<InstagramUserDB> followers_limited= this.filterList(ps.getFollowers(),status.getNextFollower());

		if(followers_limited.isEmpty()) {

			List<InstagramUserDB> following_limited= this.filterList(ps.getFollowing(),status.getNextFollowing());

			if(!following_limited.isEmpty()) {

				this.FetchUserData(following_limited);

				this.status.setNextFollowing(following_limited.get(following_limited.size()-1).getUsername());
			}
			else {
				//aggiornare status , cosa mettere ?? se username inesistente problemi ??

				this.status.setNextFollower("finito");  

				this.status.setNextFollowing("finito");         

			}
		}

		else {

			this.FetchUserData(followers_limited);

			this.status.setNextFollower(followers_limited.get(followers_limited.size()-1).getUsername());

		}

		System.out.println("\n[questa parte della ricerca è terminata , completala più tardi]\n");
		
		this.statusService.inserisci(status);

		return ;
	}

	public List<InstagramUserDB> filterList(List<InstagramUserDB> usersList,String nextUsername) {

		int index=0;

		for(InstagramUserDB user: usersList) {
			if(user.getUsername().equals(nextUsername)) {
				index=usersList.indexOf(user);
				break;
			}
		}

		List<InstagramUserDB> usersLimited = usersList.stream().skip(index).limit(1000).collect(Collectors.toList());

		//debug

		System.out.println("\n"+usersLimited.size()+" elementi nella lista:");

		for(InstagramUserDB i:usersLimited) {
			System.out.println("["+i.getUsername()+"]");   
		}

		//fine debug

		if(usersLimited.size()==1) usersLimited.clear();   //per evitare di avere sempre un elemento nella lista

		return usersLimited;

	}

	public void FetchUserData(List<InstagramUserDB> users_limit ) throws Exception{

		//salvo i dati di ogni following
		InstagramSearchUsernameResult result;

		for (InstagramUserDB user: users_limit) {

			result = instagram.sendRequest(new InstagramSearchUsernameRequest(user.getUsername()));
		
			InstagramUserDB userDB;                     //utente da aggiornare con i dati da insta
			
			if(!result.getStatus().equals("fail")) {

				userDB = SetSingleUserData(result.getUser()); //setto i dati di ogni followed (following)
			}
			else {
				userDB= new InstagramUserDB(user.getUsername(),user.getPk());  //se un utente non esiste più su insta, o se ci sono stati problemi nel fetch dei dati
				
				userDB.setFullName("fail");
			}
			
			this.instaUserDBService.inserisci(userDB);

			TimeUnit.SECONDS.sleep(1); //simula l'uso di un utente

		}

		return;

	}

	public InstagramSearchUsernameResult FetchSubjectData(String account) {

		InstagramSearchUsernameResult userResult=null;

		try {
			userResult = this.instagram.sendRequest(new InstagramSearchUsernameRequest(account));

			InstagramUserDB user=SetSingleUserData(userResult.getUser());

			this.instaUserDBService.inserisci(user);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return userResult;
	}

	public List<InstagramUserDB> FetchFollowersData(InstagramSearchUsernameResult userResult) throws Exception {

		InstagramGetUserFollowersResult users;

		List<InstagramUserSummary> UserfollowersList=new ArrayList<>();   //lista che aggiorno prendendo i follower dalle api

		String nextMaxId = null;

		while (true) {

			users = instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk(), nextMaxId));

			System.out.println("[fetched followers: "+users.getUsers().size()+"]");

			UserfollowersList.addAll(users.getUsers());

			nextMaxId = users.getNext_max_id();

			if (nextMaxId == null) {
				break;
			}

		}

		List<InstagramUserDB> followers=this.insertSingleUser(UserfollowersList);               //lista che inserisco nel db

		this.status.setNextFollower(followers.get(0).getUsername());

		return followers;

	}

	public List<InstagramUserDB> FetchFollowingData(InstagramSearchUsernameResult userResult) throws Exception {

		InstagramGetUserFollowersResult users;

		List<InstagramUserSummary> UserfollowingList=new ArrayList<>();   //lista che aggiorno prendendo i follower dalle api

		String nextMaxId = null;

		while (true) {

			users = instagram.sendRequest(new InstagramGetUserFollowingRequest(userResult.getUser().getPk(), nextMaxId));

			System.out.println("[fetched following: "+users.getUsers().size()+"]");

			UserfollowingList.addAll(users.getUsers());

			nextMaxId = users.getNext_max_id();

			if (nextMaxId == null) {
				break;
			}

		}

		List<InstagramUserDB> following=this.insertSingleUser(UserfollowingList);                //lista che inserisco nel db

		this.status.setNextFollowing(following.get(0).getUsername());

		return following;

	}

	public List<InstagramUserDB> insertSingleUser(List<InstagramUserSummary> userList){

		List<InstagramUserDB> list=new ArrayList<>();    

		for (InstagramUserSummary ius : userList) {

			InstagramUserDB user = new InstagramUserDB(ius.getUsername(),ius.getPk());

			list.add(user);

			this.instaUserDBService.inserisci(user);  //inserisco il singolo follower nel db 

		}

		return list;
	}

	//non prende tutti i post appositamente potrebbero essere troppi (da pensare)
	public List<Media> FetchMediaData(ProfileSubject p,InstagramSearchUsernameResult userResult) throws Exception{

		InstagramFeedResult feed= instagram.sendRequest(new InstagramUserFeedRequest(userResult.getUser().getPk()));
		List<InstagramFeedItem> lista=feed.getItems();

		System.out.println("[fetched post items: "+lista.size()+"]");

		List<Media> media=new ArrayList<>();

		//salvo i post dell'utente
		for(InstagramFeedItem item: lista) {

			Media m=SetSingleMediaData(item,p);

			media.add(m);

			this.mediaService.inserisci(m);
		}

		return media;
	}

	public List<Comment> FetchComments(String id) throws Exception{

		List<Comment> comments=new ArrayList<>();

		String nextMaxId = null;
		do {

			InstagramGetMediaCommentsRequest request = new InstagramGetMediaCommentsRequest(id, nextMaxId);
			InstagramGetMediaCommentsResult commentsResult = instagram.sendRequest(request);

			List<InstagramComment> Instacomments = commentsResult.getComments();

			System.out.println("[fetched comments: "+Instacomments.size()+"]");

			String lastComment = null;

			for (InstagramComment Instacomment : Instacomments) {

				Comment c=SetSingleCommentData(Instacomment);

				comments.add(c);

				//salvo ogni commento
				this.commentService.inserisci(c);

				if(lastComment == null) lastComment = String.valueOf(Instacomment.getPk());

				TimeUnit.SECONDS.sleep(1);

			}
			nextMaxId = commentsResult.getNext_max_id();

			if(nextMaxId != null) nextMaxId = lastComment;

		} while(nextMaxId != null);

		return comments;
	}


	public Media SetSingleMediaData(InstagramFeedItem item,ProfileSubject p) {

		Media media=new Media();

		media.setPk(item.getPk());

		media.setNum_comments(item.getComment_count());

		media.setNum_likes(item.getLike_count());

		media.SetTimestamp(item.getTaken_at());

		if(item.getLocation()!=null)
			media.setLocation(item.getLocation().getCity());

		if(item.getCaption()!=null)
			media.setCaption(item.getCaption().getText());

		media.setOwner(p);

		List<Comment> comments=null;
		try {
			comments=FetchComments(item.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		media.setComments(comments);

		return media;
	}

	public Comment SetSingleCommentData(InstagramComment ic) throws Exception {

		Comment c=new Comment();

		c.setPk(ic.getPk());;

		c.setText(ic.getText());

		c.setTimestamp(ic.getCreated_at());

		//devo salvare l'owner
		/*InstagramSearchUsernameResult result = instagram.sendRequest(new InstagramSearchUsernameRequest(ic.getUser().getUsername()));
		InstagramUserDB user=SetSingleUserData(result.getUser());

		this.instaUserDBService.inserisci(user);

		//lo setto come owner del commento
		c.setOwner(user);*/

		return c;

	}


	public InstagramUserDB SetSingleUserData(InstagramUser user) {

		InstagramUserDB tmp=new InstagramUserDB();

		tmp.setUsername(user.getUsername());

		if(user.getFull_name().length()>254) {
			tmp.setFullName("too long");
		}
		else tmp.setFullName(user.getFull_name());
		
		tmp.setPk(user.getPk());

		tmp.setPrivate(user.is_private);

		tmp.setVerified(user.is_verified);

		tmp.setBio(user.getBiography());

		tmp.setNum_followers(user.getFollower_count());

		tmp.setNum_following(user.getFollowing_count());

		tmp.setNum_posts(user.getMedia_count());

		tmp.setLocation(user.getCity_name());
		
		if(user.getExternal_url().length()>254) {
			tmp.setExternal_url("too long");                          //se l'url contenuto nella bio è troppo lungo per poter essere salvato nel db
		}
		else tmp.setExternal_url(user.getExternal_url());

		tmp.setHas_anonymous_profile_pic(user.isHas_anonymous_profile_picture());

		tmp.setNum_tags(user.getUsertags_count());

		return tmp;

	}

}
