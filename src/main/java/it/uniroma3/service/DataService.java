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
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.logic.InstaConfig;
import it.uniroma3.model.Comment;
import it.uniroma3.model.InstagramUser;
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
		List<InstagramUser> followers= FetchFollowersData(userResult);

		//prendo i dati degli utenti che il soggetto della ricerca segue (following)
		List<InstagramUser> following= FetchFollowingData(userResult); 


		//salvo i dati del soggetto della ricerca
		ProfileSubject ps=new ProfileSubject();
		InstagramUser user=SetSingleUserData(userResult);
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
			InstagramUser user=new InstagramUser();
			user.setUsername(userResult.getUser().getUsername());
			user.setFullName(userResult.getUser().getFull_name());
			user.setBio(userResult.getUser().getBiography());
			user.setNum_followers(userResult.getUser().getFollower_count());
			user.setNum_following(userResult.getUser().getFollowing_count());
			user.setNum_posts(userResult.getUser().getMedia_count());
			user.setPk(userResult.getUser().getPk());
			user.setPrivate(userResult.getUser().is_private);
			user.setVerified(userResult.getUser().is_verified);
			user.setLocation(userResult.getUser().getCity_name());

			this.instaUService.inserisci(user);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userResult;
	}

	public List<InstagramUser> FetchFollowersData(InstagramSearchUsernameResult userResult) throws Exception {

		InstagramGetUserFollowersResult users;

		List<InstagramUserSummary> UserfollowersList=new ArrayList<>();   //lista che aggiorno prendendo i follower dalle api
		List<InstagramUser> followers=new ArrayList<>();                  //lista che inserisco nel db

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

			InstagramUser follower = SetSingleUserData(result); //setto i dati di ogni follower 

			followers.add(follower);

			this.instaUService.inserisci(follower);  //inserisco il singolo follower nel db 

			TimeUnit.SECONDS.sleep(2); //simula l'uso di un utente

		}

		return followers;

	}

	public List<InstagramUser> FetchFollowingData(InstagramSearchUsernameResult userResult) throws Exception {

		InstagramGetUserFollowersResult users;

		List<InstagramUserSummary> UserfollowingList=new ArrayList<>();   //lista che aggiorno prendendo i follower dalle api
		List<InstagramUser> following=new ArrayList<>();                  //lista che inserisco nel db

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

			InstagramUser followed = SetSingleUserData(result); //setto i dati di ogni followed (following)

			following.add(followed);

			this.instaUService.inserisci(followed);

			TimeUnit.SECONDS.sleep(2); //simula l'uso di un utente

		}

		return following;

	}

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
                   
                    if(lastComment == null) lastComment = String.valueOf(Instacomment.getPk());
            }

            nextMaxId = commentsResult.getNext_max_id();
            if(nextMaxId != null) nextMaxId = lastComment;
        } while(nextMaxId != null);
        
        return comments;
	}
	
	public Comment SetSingleCommentData(InstagramComment ic) {
		Comment c=new Comment();
		c.setMedia_id(ic.getMedia_id());
		c.setPk(ic.getPk());;
		c.setText(ic.getText());
		c.setTimestamp(ic.getCreated_at());
		
		

		//InstagramUser followed = SetSingleUserData(result);
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

	public InstagramUser SetSingleUserData(InstagramSearchUsernameResult result) {

		InstagramUser tmp=new InstagramUser();
		tmp.setUsername(result.getUser().getUsername());
		tmp.setFullName(result.getUser().getFull_name());
		tmp.setPk(result.getUser().getPk());
		tmp.setPrivate(result.getUser().is_private);
		tmp.setVerified(result.getUser().is_verified);
		tmp.setBio(result.getUser().getBiography());
		tmp.setNum_followers(result.getUser().getFollower_count());
		tmp.setNum_following(result.getUser().getFollowing_count());
		tmp.setNum_posts(result.getUser().getMedia_count());
		tmp.setLocation(result.getUser().getCity_name());

		return tmp;

	}


}
