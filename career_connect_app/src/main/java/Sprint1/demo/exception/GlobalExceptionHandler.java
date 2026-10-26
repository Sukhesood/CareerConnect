
package Sprint1.demo.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    GlobalExceptionHandler.class);

    private ModelAndView showError(
            int status,
            String title,
            String message) {

        ModelAndView page = new ModelAndView("error");

        page.setStatus(HttpStatus.valueOf(status));
        page.addObject("status", status);
        page.addObject("errorTitle", title);
        page.addObject("errorMessage", message);

        return page;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleInvalidInput(
            IllegalArgumentException ex) {

        return showError(
                400,
                "Invalid Request",
                "The information provided is invalid.");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleLargeFile(
            MaxUploadSizeExceededException ex) {

        return showError(
                413,
                "File Too Large",
                "Your resume exceeds the allowed upload size. "
                + "Maximum file size is 4 MB.");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ModelAndView handleHttpError(
            ResponseStatusException ex) {

        int status = ex.getStatusCode().value();

        String title;
        String message;

        switch (status) {
            case 400:
                title = "Bad Request";
                message = "Your request could not be processed.";
                break;

            case 401:
                title = "Unauthorized";
                message = "Please log in to continue.";
                break;

            case 403:
                title = "Access Denied";
                message = "You do not have permission "
                        + "to access this resource.";
                break;

            case 404:
                title = "Page Not Found";
                message = "The requested page or file "
                        + "could not be found.";
                break;

            default:
                title = "Request Error";
                message = "Unable to complete your request.";
        }

        return showError(status, title, message);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnexpectedError(
            Exception ex,
            HttpServletRequest request) {

        logger.error(
                "Unexpected error at {}",
                request.getRequestURI(),
                ex);

        return showError(
                500,
                "Internal Server Error",
                "Something went wrong. "
                + "Please try again later.");
    }
}
