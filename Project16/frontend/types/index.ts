export interface User {
  id: string;
  email: string;
  username: string;
  is_designer: boolean;
  avatar: string | null;
  bio: string | null;
  created_at: string;
  updated_at: string | null;
}

export interface Work {
  id: string;
  title: string;
  description: string;
  category: string;
  price: number;
  tags: string[];
  images: string[];
  is_premium: boolean;
  download_url: string | null;
  designer_id: string;
  designer_name: string;
  designer_avatar: string | null;
  likes_count: number;
  favorites_count: number;
  comments_count: number;
  views_count: number;
  is_active: boolean;
  created_at: string;
  updated_at: string | null;
}

export interface Category {
  id: string;
  name: string;
  description: string | null;
  icon: string | null;
  work_count: number;
  created_at: string;
}

export interface Comment {
  id: string;
  work_id: string;
  user_id: string;
  user_name: string;
  user_avatar: string | null;
  content: string;
  created_at: string;
}

export interface OrderItem {
  work_id: string;
  title: string;
  price: number;
  image_url: string | null;
}

export interface Order {
  id: string;
  user_id: string;
  order_number: string;
  items: OrderItem[];
  total_amount: number;
  status: "pending" | "paid" | "cancelled" | "completed";
  created_at: string;
  updated_at: string | null;
}
