import {
  debounce as lodashDebounce,
  throttle as lodashThrottle,
} from "lodash-es";

export const debounce = (fn, delay = 300, options = {}) => {
  return lodashDebounce(fn, delay, options);
};

export const throttle = (fn, delay = 300, options = {}) => {
  return lodashThrottle(fn, delay, options);
};

export const formatTime = (time) => {
  if (!time) return "00:00:00";
  const date = new Date(time);
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const seconds = String(date.getSeconds()).padStart(2, "0");
  return `${hours}:${minutes}:${seconds}`;
};

export const formatCountdown = (milliseconds) => {
  if (milliseconds <= 0)
    return { days: "00", hours: "00", minutes: "00", seconds: "00", total: 0 };

  const total = milliseconds;
  const days = String(
    Math.floor(milliseconds / (1000 * 60 * 60 * 24)),
  ).padStart(2, "0");
  const hours = String(
    Math.floor((milliseconds % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)),
  ).padStart(2, "0");
  const minutes = String(
    Math.floor((milliseconds % (1000 * 60 * 60)) / (1000 * 60)),
  ).padStart(2, "0");
  const seconds = String(
    Math.floor((milliseconds % (1000 * 60)) / 1000),
  ).padStart(2, "0");

  return { days, hours, minutes, seconds, total };
};

export const formatPrice = (price) => {
  if (price === undefined || price === null) return "0.00";
  return Number(price).toFixed(2);
};

export const generateOrderNo = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  const hours = String(now.getHours()).padStart(2, "0");
  const minutes = String(now.getMinutes()).padStart(2, "0");
  const seconds = String(now.getSeconds()).padStart(2, "0");
  const random = String(Math.floor(Math.random() * 1000000)).padStart(6, "0");
  return `${year}${month}${day}${hours}${minutes}${seconds}${random}`;
};
