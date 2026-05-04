from django.contrib import admin
from apps.collection.models import Collection


@admin.register(Collection)
class CollectionAdmin(admin.ModelAdmin):
    list_display = ['id', 'user', 'item', 'created_at']
    list_filter = ['created_at']
    search_fields = ['user__username', 'item__title']
    ordering = ['-created_at']
