// Deprecated
export function formatUTCDate(dateString: string) {
  const date = dateString.split("T")[0];
  const time = dateString.split("T")[1].split(":").slice(0, 2).join(":");
  return `${date} ${time}`;
}
