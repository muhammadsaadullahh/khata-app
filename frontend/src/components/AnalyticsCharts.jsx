import { Bar, Doughnut, Line } from 'react-chartjs-2';
import { ArcElement, BarElement, CategoryScale, Chart as ChartJS, Legend, LinearScale, LineElement, PointElement, Tooltip } from 'chart.js';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, BarElement, ArcElement, Tooltip, Legend);
const colors = ['#0d6b5f', '#d49443', '#5b8def', '#b34738', '#7d8c88', '#8d6bb8'];
const options = { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom', labels: { usePointStyle: true, boxWidth: 8 } } } };

export function TrendChart({ data }) {
  return <div className="chart-wrap"><Line data={{ labels: data.map((item) => item.period), datasets: [{ label: 'Income', data: data.map((item) => item.income), borderColor: '#0d6b5f', backgroundColor: '#0d6b5f22', fill: true, tension: .35 }, { label: 'Outcome', data: data.map((item) => item.outcome), borderColor: '#d49443', backgroundColor: '#d4944322', fill: true, tension: .35 }] }} options={options} /></div>;
}

export function CategoryChart({ data }) {
  return <div className="chart-wrap chart-small"><Doughnut data={{ labels: data.map((item) => item.category), datasets: [{ data: data.map((item) => item.total), backgroundColor: colors, borderWidth: 0 }] }} options={options} /></div>;
}

export function IncomeOutcomeChart({ data }) {
  return <div className="chart-wrap chart-small"><Bar data={{ labels: data.map((item) => item.period), datasets: [{ label: 'Income', data: data.map((item) => item.income), backgroundColor: '#0d6b5f' }, { label: 'Outcome', data: data.map((item) => item.outcome), backgroundColor: '#d49443' }] }} options={options} /></div>;
}
