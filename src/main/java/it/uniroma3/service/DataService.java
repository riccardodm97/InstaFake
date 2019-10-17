package it.uniroma3.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

@Service
public class DataService {

	@Autowired
	private InstaConfig instaconf;

	@Autowired 
	private InstagramUserService instaUService;

	@Autowired
	private ProfileSubjectService profileService;
	
	@Autowired
	private MediaService mediaService;
	
	@Autowired 
	private CommentService commentService;

	private Instagram4j instagram;


	public void Search(String account) throws Exception {

		//prendo l'istanza singleton della classe instagram delle api per poterla utilizzare
		this.instagram =instaconf.config();

		//prendo i dati dell'account instagram su cui sto conducendo la ricerca
		InstagramSearchUsernameResult userResult= FetchSubjectData(account);

		//prendo i dati dei follower dell'utente 
		List<InstagramUserDB> followers= FetchFollowersData(userResult);

		//prendo i dati degli utenti che il soggetto della ricerca segue (following)
		List<InstagramUserDB> following= FetchFollowingData(userResult); 


		//salvo i dati del soggetto della ricerca
		ProfileSubject ps=new ProfileSubject();
		InstagramUserDB user=SetSingleUserData(userResult.getUser());
		ps.setUsername(userResult.getUser().getUsername());
		ps.setProfile(user);
		ps.setFollowers(followers);
		ps.setFollowing(following);
		
		this.profileService.inserisci(ps);

		//prendo i dati relativi ai post dell'utente
		ProfileSubject p=this.profileService.cercaPerUsername(userResult.getUser().getUsername());
		List<Media> media=FetchMediaData(p,userResult);
		
		//aggiorno il soggetto della ricerca con i post appena ottenuti
		p.setPosts(media);
		this.profileService.inserisci(p);
		
		//test
		/*System.out.println("ID for "+ userResult.getUser().getFull_name() + " is " + userResult.getUser().getPk());
		System.out.println("Number of followers: " + userResult.getUser().getFollower_count());
		System.out.println("Number of following: "+userResult.getUser().getFollowing_count());
		System.out.println("Number of posts: "+userResult.getUser().getMedia_count());
		System.out.println(userResult.getUser().getBiography());*/

	}

	public InstagramSearchUsernameResult FetchSubjectData(String account) {

		InstagramSearchUsernameResult userResult=null;
		try {
			userResult = this.instagram.sendRequest(new InstagramSearchUsernameRequest(account));
			InstagramUserDB user=SetSingleUserData(userResult.getUser());
			this.instaUService.inserisci(user);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userResult;
	}

	public List<InstagramUserDB> FetchFollowersData(InstagramSearchUsernameResult userResult) throws Exception {

		InstagramGetUserFollowersResult users;

		List<InstagramUserSummary> UserfollowersList=new ArrayList<>();   //lista che aggiorno prendendo i follower dalle api
		List<InstagramUserDB> followers=new ArrayList<>();                  //lista che inserisco nel db

		String nextMaxId = null;
		while (true) {
			users = instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk(), nextMaxId));
			UserfollowersList.addAll(users.getUsers());
			nextMaxId = users.getNext_max_id();
			if (nextMaxId == null) {
				break;
			}
		}


		//salvo i dati di ogni follower
		InstagramSearchUsernameResult result;

		for (InstagramUserSummary ius : UserfollowersList) {

			result = instagram.sendRequest(new InstagramSearchUsernameRequest(ius.getUsername()));

			InstagramUserDB follower = SetSingleUserData(result.getUser()); //setto i dati di ogni follower 

			followers.add(follower);

			this.instaUService.inserisci(follower);  //inserisco il singolo follower nel db 

			TimeUnit.SECONDS.sleep(1); //simula l'uso di un utente

		}

		return followers;

	}

	public List<InstagramUserDB> FetchFollowingData(InstagramSearchUsernameResult userResult) throws Exception {

		InstagramGetUserFollowersResult users;

		List<InstagramUserSummary> UserfollowingList=new ArrayList<>();   //lista che aggiorno prendendo i follower dalle api
		List<InstagramUserDB> following=new ArrayList<>();                  //lista che inserisco nel db

		String nextMaxId = null;
		while (true) {
			users = instagram.sendRequest(new InstagramGetUserFollowingRequest(userResult.getUser().getPk(), nextMaxId));
			UserfollowingList.addAll(users.getUsers());
			nextMaxId = users.getNext_max_id();
			if (nextMaxId == null) {
				break;
			}
		}

		//salvo i dati di ogni following
		InstagramSearchUsernameResult result;

		for (InstagramUserSummary ius : UserfollowingList) {

			result = instagram.sendRequest(new InstagramSearchUsernameRequest(ius.getUsername()));

			InstagramUserDB followed = SetSingleUserData(result.getUser()); //setto i dati di ogni followed (following)

			following.add(followed);

			this.instaUService.inserisci(followed);

			TimeUnit.SECONDS.sleep(1); //simula l'uso di un utente

		}

		return following;

	}
	
	//non prende tutti i post appositamente potrebbero essere troppi (da pensare)
	public List<Media> FetchMediaData(ProfileSubject p,InstagramSearchUsernameResult userResult) throws Exception{
		
		InstagramFeedResult feed= instagram.sendRequest(new InstagramUserFeedRequest(userResult.getUser().getPk()));
		List<InstagramFeedItem> lista=feed.getItems();
		
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
	
	public Comment SetSingleCommentData(InstagramComment ic) throws Exception {
		Comment c=new Comment();
		c.setMedia_id(ic.getMedia_id());
		c.setPk(ic.getPk());;
		c.setText(ic.getText());
		c.setTimestamp(ic.getCreated_at());
		//devo salvare l'owner
		InstagramSearchUsernameResult result = instagram.sendRequest(new InstagramSearchUsernameRequest(ic.getUser().getUsername()));
		InstagramUserDB user=SetSingleUserData(result.getUser());
		this.instaUService.inserisci(user);
		//lo setto come owner del commento
		c.setOwner(user);
		
		return c;
		
	}

	public Media SetSingleMediaData(InstagramFeedItem item,ProfileSubject p) {
		Media media=new Media();
		media.setPk(item.getPk());
		media.setId(item.getId());
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

	public InstagramUserDB SetSingleUserData(InstagramUser user) {

		InstagramUserDB tmp=new InstagramUserDB();
		tmp.setUsername(user.getUsername());
		tmp.setFullName(user.getFull_name());
		tmp.setPk(user.getPk());
		tmp.setPrivate(user.is_private);
		tmp.setVerified(user.is_verified);
		tmp.setBio(user.getBiography());
		tmp.setNum_followers(user.getFollower_count());
		tmp.setNum_following(user.getFollowing_count());
		tmp.setNum_posts(user.getMedia_count());
		tmp.setLocation(user.getCity_name());

		return tmp;

	}


}
