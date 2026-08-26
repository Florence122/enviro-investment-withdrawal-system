import { useState, useEffect } from 'react';
import './App.css';

const API_BASE = 'http://localhost:8080/api';

function App() {
  const [investorId, setInvestorId] = useState(1);
  const [portfolio, setPortfolio] = useState(null);
  const [withdrawals, setWithdrawals] = useState([]);
  const [amount, setAmount] = useState('');
  const [type, setType] = useState('SAVINGS');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);

  const fetchPortfolio = async () => {
    try {
      const res = await fetch(`${API_BASE}/investors/${investorId}/portfolio`);
      if (!res.ok) throw new Error('Investor not found');
      const data = await res.json();
      setPortfolio(data);
    } catch (err) {
      setPortfolio(null);
      setMessage(err.message);
    }
  };

  const fetchWithdrawals = async () => {
    try {
      const res = await fetch(`${API_BASE}/investors/${investorId}/withdrawals`);
      const data = await res.json();
      setWithdrawals(data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchPortfolio();
    fetchWithdrawals();
  }, [investorId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setLoading(true);
    try {
      const res = await fetch(`${API_BASE}/withdrawals`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          investorId: Number(investorId),
          amount: Number(amount),
          type: type,
        }),
      });
      const data = await res.json();
      if (!res.ok) {
        setMessage(data.message || 'Withdrawal failed.');
      } else {
        setMessage(`Withdrawal approved: R${data.amount}`);
        setAmount('');
        fetchWithdrawals();
      }
    } catch (err) {
      setMessage('Something went wrong: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDownloadCsv = () => {
    const headers = ['ID', 'Amount', 'Type', 'Status', 'Created At'];
    const rows = withdrawals.map((w) => [w.id, w.amount, w.type, w.status, w.createdAt]);
    const csvContent =
      [headers, ...rows].map((row) => row.join(',')).join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `withdrawals-investor-${investorId}.csv`;
    link.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div style={{ maxWidth: 700, margin: '0 auto', padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>Enviro365 Investor Portal</h1>

      <div style={{ marginBottom: '1rem' }}>
        <label>
          Investor ID:{' '}
          <input
            type="number"
            value={investorId}
            onChange={(e) => setInvestorId(e.target.value)}
            style={{ width: 60 }}
          />
        </label>
      </div>

      {portfolio && (
        <div style={{ border: '1px solid #ccc', padding: '1rem', marginBottom: '1.5rem' }}>
          <h2>{portfolio.name}</h2>
          <p>Age: {portfolio.age}</p>
          <h3>Products</h3>
          <ul>
            {portfolio.products.map((p, i) => (
              <li key={i}>
                {p.name} — R{p.balance}
              </li>
            ))}
          </ul>
        </div>
      )}

      <h2>Request a Withdrawal</h2>
      <form onSubmit={handleSubmit} style={{ marginBottom: '1.5rem' }}>
        <div style={{ marginBottom: '0.5rem' }}>
          <label>
            Amount:{' '}
            <input
              type="number"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              required
            />
          </label>
        </div>
        <div style={{ marginBottom: '0.5rem' }}>
          <label>
            Type:{' '}
            <select value={type} onChange={(e) => setType(e.target.value)}>
              <option value="SAVINGS">Savings</option>
              <option value="RETIREMENT">Retirement</option>
              <option value="GENERAL">General</option>
            </select>
          </label>
        </div>
        <button type="submit" disabled={loading}>
          {loading ? 'Submitting...' : 'Submit Withdrawal'}
        </button>
      </form>

      {message && <p style={{ color: message.includes('approved') ? 'green' : 'red' }}>{message}</p>}

      <h2>Withdrawal History</h2>
      <button onClick={handleDownloadCsv} style={{ marginBottom: '0.5rem' }}>
        Download CSV
      </button>
      <table border="1" cellPadding="6" style={{ borderCollapse: 'collapse', width: '100%' }}>
        <thead>
          <tr>
            <th>ID</th>
            <th>Amount</th>
            <th>Type</th>
            <th>Status</th>
            <th>Date</th>
          </tr>
        </thead>
        <tbody>
          {withdrawals.map((w) => (
            <tr key={w.id}>
              <td>{w.id}</td>
              <td>R{w.amount}</td>
              <td>{w.type}</td>
              <td>{w.status}</td>
              <td>{new Date(w.createdAt).toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default App;