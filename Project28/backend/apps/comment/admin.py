from django.contrib import admin
from apps.comment.models import Comment


@admin.register(Comment)
class CommentAdmin(admin.ModelAdmin):
    list_display = ['id', 'item', 'user', 'parent', 'content', 
                    'is_violation', 'created_at']
    list_filter = ['is_violation', 'created_at']
    search_fields = ['content', 'user__username', 'item__title']
    ordering = ['-created_at']
    readonly_fields = ['created_at', 'updated_at']
    actions = ['mark_violation', 'unmark_violation']
    
    def mark_violation(self, request, queryset):
        queryset.update(is_violation=True)
    mark_violation.short_description = '标记为违规'
    
    def unmark_violation(self, request, queryset):
        queryset.update(is_violation=False)
    unmark_violation.short_description = '取消违规标记'
