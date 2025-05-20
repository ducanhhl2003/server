package book.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import book.service.impl.CommentService;
import book.service.impl.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomSecurityExpression {
    private final RoleConfig roleConfig;
    private final PostService postService;
    private final CommentService commentService;

    /**
     * @param(HttpServletRequest) request
     * Bước 1: Trích xuất giá trị URI và convert về dạng api.v1.entity.method cho giống file role.properties
     * Bước 2: Tìm kiếm permission dựa trên URI qua RoleConfig
     * Bước 3: Truyền giá trị permission ở trên vào hàm roleGroupHasFunction, trong đó sẽ
     * chọc vào repository để tìm xem liệu role của user authenticated thoong qua SecurityContextHolder
     * có quyền tương tự với permission ở trên hay không
     * @return boolean
     */

    public boolean fileRole(HttpServletRequest request) {
        System.out.println("Evaluating fileRole for: " + request.getRequestURI());
        String uri = request.getRequestURI();
        String requiredPermission = roleConfig.getFunctionByUri(uri);

        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(roleGroup -> roleConfig.roleGroupHasFunction(roleGroup, requiredPermission));
    }

    /**
     * @param(HttpServletRequest) request
     * Bước 1: Trích xuất giá trị id của post || comment và để tìm kiếm existing entity
     * Bước 2: So sánh created_by thông qua AuditorAware để kiểm tra xem author có trùng với
     * current authenticated user trong SecurityContextHolder hay không
     * -> Chỉ user chính chủ mới được quyền sửa xoá bài của mình
     * @return boolean
     */

//    public boolean isPostOwner(Long postId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication instanceof AnonymousAuthenticationToken) {
//            return false;
//        }
//        PostResponse existingPost = postService.getPostById(postId);
//        if (isSuperiorAuthorities(authentication)) {
//            return true;
//        }
//        return existingPost.getAuditor().getCreatedBy()
//                .equals(((User) authentication.getPrincipal()).getUsername());
//    }
//
//    public boolean isCommentOwner(Long commentId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication instanceof AnonymousAuthenticationToken) {
//            return false;
//        }
//        if (isSuperiorAuthorities(authentication)) {
//            return true;
//        }
//        return commentService.getCommentById(commentId).getAuditor().getCreatedBy()
//                .equals(((User) authentication.getPrincipal()).getUsername());
//    }
//
//    private boolean isSuperiorAuthorities(Authentication authentication){
//        return authentication.getAuthorities().stream().anyMatch(grantedAuthority ->
//                grantedAuthority.getAuthority().equals("ROLE_ADMIN")
//                        || grantedAuthority.getAuthority().equals("ROLE_MANAGER")
//        );
//    }

}