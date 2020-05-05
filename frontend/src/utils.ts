// Deprecated
export function formatISODate(dateString: string) {
  const date = new Date(dateString);
  const options = {
    year: 'numeric',
    month: 'numeric',
    day: 'numeric',
    hour: 'numeric',
    minute: 'numeric',
    hour12: false
  };
  return new Intl.DateTimeFormat('pt-PT', options).format(date);
}
