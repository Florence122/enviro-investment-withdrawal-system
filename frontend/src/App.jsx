import { useState, useEffect } from 'react';
import './App.css';

const API_BASE = 'http://localhost:8080/api';

const formatCurrency = (value) =>
  Number(value).toLocaleString('en-ZA', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

function App() {
  const [investorId, setInvestorId] = useState(1);
  const [portfolio, setPortfolio] = useState(null);
  const [withdrawals, setWithdrawals] = useState([]);
  const [amount, setAmount] = useState('');
  const [type, setType] = useState('SAVINGS');
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('success');
  const [loading, setLoading] = useState(false);

  const fetchPortfolio = async () => {
    try {
      const res = await fetch(`${API_BASE}/investors/${investorId}/portfolio`);
      if (!res.ok) throw new Error('Investor not found');
      const data = await res.json();
      setPortfolio(data);
    } catch (err) {
      setPortfolio(null);
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
    setMessage('');
  }, [investorId]);

  const totalBalance =
    portfolio?.products?.reduce((sum, p) => sum + Number(p.balance), 0) ?? 0;

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
        setMessageType('error');
        setMessage(data.message || 'Withdrawal failed.');
      } else {
        setMessageType('success');
        setMessage(`Withdrawal approved — R${formatCurrency(data.amount)} (${data.type.toLowerCase()})`);
        setAmount('');
        fetchWithdrawals();
      }
    } catch (err) {
      setMessageType('error');
      setMessage('Something went wrong: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDownloadCsv = () => {
    const headers = ['ID', 'Amount', 'Type', 'Status', 'Created At'];
    const rows = withdrawals.map((w) => [w.id, w.amount, w.type, w.status, w.createdAt]);
    const csvContent = [headers, ...rows].map((row) => row.join(',')).join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `withdrawals-investor-${investorId}.csv`;
    link.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="bank-app">
      <header className="bank-header">
        <div className="bank-wordmark">
          <span className="mark">Enviro365</span>
          <span className="sub">Investor Portal</span>
        </div>
        <div className="investor-picker">
          <label htmlFor="investorId">Account</label>
          <input
            id="investorId"
            type="number"
            value={investorId}
            onChange={(e) => setInvestorId(e.target.value)}
          />
        </div>
      </header>

      <main className="bank-main">
        {portfolio && (
          <div className="account-card">
            <div className="label">Account Holder</div>
            <div className="name">{portfolio.name}</div>
            <div className="meta">Age {portfolio.age} · Account No. {String(portfolio.id).padStart(6, '0')}</div>

            <ul className="products">
              {portfolio.products.map((p, i) => (
                <li key={i}>
                  <span>{p.name}</span>
                  <span>R{formatCurrency(p.balance)}</span>
                </li>
              ))}
            </ul>

            <div className="balance-row">
              <span className="balance-label">Total Available Balance</span>
              <span className="balance-figure">R{formatCurrency(totalBalance)}</span>
            </div>
          </div>
        )}

        <section className="panel">
          <h2>Request a Withdrawal</h2>
          <form onSubmit={handleSubmit}>
            <div className="field amount">
              <label htmlFor="amount">Amount (ZAR)</label>
              <input
                id="amount"
                type="number"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                placeholder="0.00"
                required
              />
            </div>
            <div className="field">
              <label htmlFor="type">Withdrawal Type</label>
              <select id="type" value={type} onChange={(e) => setType(e.target.value)}>
                <option value="SAVINGS">Savings</option>
                <option value="RETIREMENT">Retirement</option>
                <option value="GENERAL">General</option>
              </select>
            </div>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Processing…' : 'Submit Withdrawal'}
            </button>
          </form>

          {message && (
            <div className={`status-msg ${messageType}`}>{message}</div>
          )}
        </section>

        <section className="panel">
          <div className="panel-heading-row">
            <h2>Withdrawal History</h2>
            <button className="btn-outline" onClick={handleDownloadCsv}>
              Download CSV
            </button>
          </div>

          {withdrawals.length === 0 ? (
            <div className="empty-state">No withdrawals on record for this account yet.</div>
          ) : (
            <table className="statement-table">
              <thead>
                <tr>
                  <th>Ref.</th>
                  <th className="num">Amount</th>
                  <th>Type</th>
                  <th>Status</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>
                {withdrawals.map((w) => (
                  <tr key={w.id}>
                    <td>#{String(w.id).padStart(4, '0')}</td>
                    <td className="amount-cell">R{formatCurrency(w.amount)}</td>
                    <td>{w.type}</td>
                    <td>
                      <span className={`pill ${w.status === 'APPROVED' ? 'approved' : 'rejected'}`}>
                        {w.status}
                      </span>
                    </td>
                    <td>{new Date(w.createdAt).toLocaleString('en-ZA')}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </section>
      </main>

      <footer className="bank-footer">
        Enviro365 Investments (Pty) Ltd · Junior Developer Assessment — for demonstration purposes only
      </footer>
    </div>
  );
}

export default App;
