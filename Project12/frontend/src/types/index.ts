export interface User {
  id: number;
  username: string;
  email: string;
  phone: string;
  first_name: string;
  last_name: string;
  role: 'admin' | 'doctor' | 'resident';
  role_display: string;
  avatar: string | null;
  is_verified: boolean;
  is_active: boolean;
  is_staff: boolean;
  date_joined: string;
  created_at: string;
  updated_at: string;
}

export interface Community {
  id: number;
  code: string;
  name: string;
  address: string | null;
  province: string | null;
  city: string | null;
  district: string | null;
  phone: string | null;
  manager: number | null;
  manager_name: string;
  description: string | null;
  status: string;
  status_display: string;
  resident_count: number;
  full_address: string;
  created_at: string;
  updated_at: string;
}

export interface Resident {
  id: number;
  user: number;
  username: string;
  community: number | null;
  community_name: string;
  name: string;
  id_card: string;
  gender: 'male' | 'female' | 'unknown';
  gender_display: string;
  birth_date: string | null;
  age: number | null;
  phone: string | null;
  emergency_contact: string | null;
  emergency_phone: string | null;
  address: string | null;
  blood_type: string;
  blood_type_display: string;
  marital_status: string;
  marital_status_display: string;
  occupation: string | null;
  nationality: string;
  allergy_history: string | null;
  chronic_diseases: string | null;
  is_insured: boolean;
  insurance_number: string | null;
  is_active: boolean;
  created_at: string;
  updated_at: string;
  created_by: number | null;
}

export interface HealthRecord {
  id: number;
  resident: number;
  resident_name: string;
  record_date: string;
  record_type: string;
  record_type_display: string;
  height: number | null;
  weight: number | null;
  bmi: number | null;
  blood_pressure_systolic: number | null;
  blood_pressure_diastolic: number | null;
  blood_pressure_display: string | null;
  heart_rate: number | null;
  body_temperature: number | null;
  respiratory_rate: number | null;
  blood_glucose: number | null;
  blood_glucose_type: string | null;
  blood_glucose_type_display: string;
  cholesterol: number | null;
  triglyceride: number | null;
  hdl: number | null;
  ldl: number | null;
  vision_left: string | null;
  vision_right: string | null;
  hearing_left: string | null;
  hearing_right: string | null;
  oral_health: string | null;
  skin_condition: string | null;
  abnormal_findings: string | null;
  diagnosis: string | null;
  recommendations: string | null;
  next_checkup_date: string | null;
  doctor: number | null;
  doctor_name: string;
  hospital: string | null;
  notes: string | null;
  attachments: string | null;
  created_at: string;
  updated_at: string;
  created_by: number | null;
}

export interface Appointment {
  id: number;
  resident: number;
  resident_name: string;
  appointment_type: string;
  appointment_type_display: string;
  appointment_date: string;
  appointment_time: string;
  doctor: number | null;
  doctor_name: string;
  department: string | null;
  description: string | null;
  status: string;
  status_display: string;
  notes: string | null;
  cancel_reason: string | null;
  completed_at: string | null;
  created_at: string;
  updated_at: string;
  created_by: number | null;
}

export interface MedicalRecord {
  id: number;
  resident: number;
  resident_name: string;
  visit_date: string;
  visit_type: string;
  visit_type_display: string;
  doctor: number | null;
  doctor_name: string;
  department: string | null;
  hospital: string | null;
  chief_complaint: string;
  present_illness: string | null;
  past_history: string | null;
  allergy_history: string | null;
  physical_examination: string | null;
  auxiliary_examination: string | null;
  diagnosis: string;
  treatment_plan: string | null;
  prescription: string | null;
  follow_up_requirements: string | null;
  next_visit_date: string | null;
  notes: string | null;
  attachments: string | null;
  created_at: string;
  updated_at: string;
  created_by: number | null;
}

export interface MedicationReminder {
  id: number;
  resident: number;
  resident_name: string;
  medication_name: string;
  medication_type: string | null;
  dosage: string;
  frequency: string;
  frequency_display: string;
  custom_frequency: string | null;
  reminder_times: string;
  reminder_times_list: string[];
  start_date: string;
  end_date: string | null;
  instructions: string | null;
  side_effects: string | null;
  precautions: string | null;
  prescribing_doctor: number | null;
  doctor_name: string;
  prescription_source: string | null;
  status: string;
  status_display: string;
  notes: string | null;
  logs: ReminderLog[];
  created_at: string;
  updated_at: string;
  created_by: number | null;
}

export interface ReminderLog {
  id: number;
  reminder: number;
  reminder_time: string;
  status: string;
  status_display: string;
  acknowledged_at: string | null;
  note: string | null;
  created_at: string;
}

export interface ApiResponse<T> {
  count?: number;
  next?: string | null;
  previous?: string | null;
  results?: T[];
}

export interface TokenResponse {
  refresh: string;
  access: string;
  user: User;
}

export interface LoginParams {
  username: string;
  password: string;
}
