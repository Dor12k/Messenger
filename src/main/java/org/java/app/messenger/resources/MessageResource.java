package org.java.app.messenger.resources;

import java.net.URI;
import java.util.List;

import org.java.app.messenger.model.Message;
import org.java.app.messenger.resources.bean.MessageFilterBean;
import org.java.app.messenger.services.MessageService;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;


@Path("/messages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MessageResource {
	
	MessageService messageService = new MessageService();
	
    @GET
    public List<Message> getMesseages(@BeanParam MessageFilterBean filterBean) {
        
    	if( 0 < filterBean.getYear() )
        	return messageService.getAllMessageForYear(filterBean.getYear());
        if( 0 < filterBean.getStart() && 0 < filterBean.getSize() )
        	return messageService.getAllMessagePaginated(filterBean.getStart(), filterBean.getSize());
        
    	return messageService.getAllMessages();
    }

    @POST
    public Message addMessage(Message message) {
    	return messageService.addMessage(message);
    }

    @PUT
    @Path("/{messageId}")
    public Message updateMessage(@PathParam("messagrId") long id, Message message) {
    	message.setId(id);
    	return messageService.updateMessage(message);
    }
    
    @DELETE
    @Path("/{messageId}")
    public void deleteMessage(@PathParam("messagrId") long id, Message message) {
    	messageService.removeMessage(id);
    }
    
    @GET
    @Path("/{messageId}")
    public Message getMessage(@PathParam("messageId") long id, @Context UriInfo uriInfo) {
    	Message message = messageService.getMessage(id);
    			message.addLink(getUriForSelf(uriInfo, message), "self");
    			message.addLink(getUriForProfile(uriInfo, message), "profile");
    			message.addLink(getUriForComments(uriInfo, message), "comments");

    	return message;
    }

	private String getUriForComments(UriInfo uriInfo, Message message) {
		URI uri = uriInfo.getBaseUriBuilder()
				.path(MessageResource.class)
				.path(MessageResource.class, "getCommentResource")
				.path(CommentResource.class)
				.resolveTemplate("messageId", message.getId())
				.build();
		return uri.toString();
	}

	private String getUriForProfile(UriInfo uriInfo, Message message) {
		URI uri = uriInfo.getBaseUriBuilder().path(ProfileResource.class).path(message.getAuthor()).build();
		return uri.toString();
	}

	private String getUriForSelf(UriInfo uriInfo, Message message) {
		String uri = uriInfo.getBaseUriBuilder()
    				.path(MessageResource.class).path(Long.toString(message.getId())).build().toString();
		return uri;
	}
    
    @Path("/{messageId}/comments")
    public CommentResource getCommentResource() {
    	return new CommentResource();
    }


}
