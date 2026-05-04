from rest_framework import serializers
from apps.comment.models import Comment
from apps.user.serializers import UserSerializer


class CommentSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)
    replies = serializers.SerializerMethodField()
    
    class Meta:
        model = Comment
        fields = ['id', 'item', 'user', 'content', 'parent', 
                  'is_violation', 'replies', 'created_at']
        read_only_fields = ['id', 'user', 'is_violation', 'created_at']
    
    def get_replies(self, obj):
        if obj.replies.exists():
            return CommentSerializer(obj.replies.filter(is_violation=False), many=True).data
        return []


class CommentCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Comment
        fields = ['item', 'content', 'parent']
