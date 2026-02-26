import React, { useState, useEffect } from 'react';
import {
    Users, Book, Search, Library, Plus,
    LayoutDashboard, Globe, ArrowRight,
    TrendingUp, BookOpen, Trash2, AlertCircle, CheckCircle,
    Sparkles, Mail
} from 'lucide-react';

const API_BASE = "http://localhost:8080/api";

export default function App() {
    const [activeTab, setActiveTab] = useState('dashboard');
    const [users, setUsers] = useState([]);
    const [books, setBooks] = useState([]);
    const [googleBooks, setGoogleBooks] = useState([]);
    const [recommendations, setRecommendations] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({ text: '', type: '' });

    useEffect(() => {
        fetchInitialData();
    }, []);

    const fetchInitialData = async () => {
        try {
            const [uRes, bRes] = await Promise.all([
                fetch(`${API_BASE}/usuarios`),
                fetch(`${API_BASE}/livros`)
            ]);
            if (uRes.ok) setUsers(await uRes.json());
            if (bRes.ok) setBooks(await bRes.json());
        } catch (err) {
            notify("Conexão com Backend falhou.", "error");
        }
    };

    const notify = (text, type = 'success') => {
        setMessage({ text, type });
        setTimeout(() => setMessage({ text: '', type: '' }), 5000);
    };

    const handleSearch = async () => {
        if (!searchTerm) return;
        setLoading(true);
        try {
            const res = await fetch(`${API_BASE}/google-books/search?titulo=${searchTerm}`);
            const data = await res.json();
            setGoogleBooks(data);
        } catch (err) {
            notify("Erro na busca Google Books", "error");
        } finally {
            setLoading(false);
        }
    };

    const handleImport = async (book) => {
        try {
            const res = await fetch(`${API_BASE}/livros`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    titulo: book.titulo,
                    autor: book.autor,
                    isbn: book.isbn,
                    dataPublicacao: "2024-01-01",
                    categoria: book.categoria
                })
            });
            if (res.ok) {
                notify(`Obra importada!`);
                fetchInitialData();
            }
        } catch (err) {
            notify("Erro ao importar", "error");
        }
    };

    const loadRecommendations = async (userId) => {
        try {
            const res = await fetch(`${API_BASE}/emprestimos/recomendacoes/${userId}`);
            if (res.ok) {
                const data = await res.json();
                setRecommendations(data);
                const user = users.find(u => u.id === userId);
                setSelectedUser(user);
                setActiveTab('recomendacoes');
            }
        } catch (err) {
            notify("Erro nas recomendações", "error");
        }
    };

    return (
        <div className="flex min-h-screen bg-slate-950 text-slate-100 font-sans selection:bg-blue-500/30">

            {/* SIDEBAR */}
            <aside className="w-72 bg-slate-900/40 border-r border-slate-800 p-8 flex flex-col h-screen sticky top-0 backdrop-blur-xl">
                <div className="flex items-center gap-3 text-blue-500 mb-12 px-2">
                    <Library size={40} strokeWidth={2.5} />
                    <h1 className="text-2xl font-black tracking-tighter text-white uppercase">Elotech</h1>
                </div>

                <nav className="space-y-3 flex-1">
                    <MenuBtn id="dashboard" icon={LayoutDashboard} label="Dashboard" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="usuarios" icon={Users} label="Usuários" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="livros" icon={BookOpen} label="Acervo Local" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="google" icon={Globe} label="Explorar Web" active={activeTab} setter={setActiveTab} />
                </nav>
            </aside>

            {/* MAIN CONTENT */}
            <main className="flex-1 p-12 overflow-y-auto relative">

                {message.text && (
                    <div className={`fixed top-8 right-8 p-5 rounded-2xl border shadow-2xl flex items-center gap-4 z-50 view-enter ${
                        message.type === 'error' ? "bg-red-950 border-red-800" : "bg-blue-950 border-blue-800"
                    }`}>
                        {message.type === 'error' ? <AlertCircle size={24} /> : <CheckCircle size={24} />}
                        <span className="text-sm font-bold">{message.text}</span>
                    </div>
                )}

                {activeTab === 'dashboard' && (
                    <div className="space-y-12 view-enter">
                        <header>
                            <h2 className="text-6xl font-black text-white tracking-tighter">Resumo</h2>
                            <p className="text-slate-500 mt-4 text-xl max-w-2xl font-medium leading-relaxed italic">
                                Indicadores operacionais da biblioteca.
                            </p>
                        </header>
                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-10">
                            <StatCard title="Livros no Acervo" value={books.length} icon={Book} color="text-blue-500" />
                            <StatCard title="Membros Registrados" value={users.length} icon={Users} color="text-emerald-500" />
                        </div>
                    </div>
                )}

                {activeTab === 'usuarios' && (
                    <div className="space-y-10 view-enter">
                        <h2 className="text-4xl font-bold text-white tracking-tight">Membros</h2>
                        <div className="bg-slate-900 border border-slate-800 rounded-[2.5rem] overflow-hidden shadow-2xl">
                            <table className="w-full text-left">
                                <thead className="bg-slate-800/40 text-slate-500 text-[10px] font-bold uppercase tracking-[0.4em]">
                                <tr>
                                    <th className="px-10 py-6">Nome</th>
                                    <th className="px-10 py-6">E-mail</th>
                                    <th className="px-10 py-6 text-right">Inteligência</th>
                                </tr>
                                </thead>
                                <tbody className="divide-y divide-slate-800/50">
                                {users.map(u => (
                                    <tr key={u.id} className="hover:bg-slate-800/30 transition-colors">
                                        <td className="px-10 py-6 font-bold text-slate-200">{u.nome}</td>
                                        <td className="px-10 py-6 text-slate-400">{u.email}</td>
                                        <td className="px-10 py-6 text-right">
                                            <button
                                                onClick={() => loadRecommendations(u.id)}
                                                className="text-blue-400 hover:text-white flex items-center gap-2 text-xs font-bold float-right bg-blue-500/10 px-4 py-2 rounded-xl transition-all"
                                            >
                                                <Sparkles size={14} /> Recomendar
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}

                {activeTab === 'livros' && (
                    <div className="space-y-10 view-enter">
                        <h2 className="text-4xl font-bold text-white tracking-tight">Acervo Local</h2>
                        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-8">
                            {books.map(b => (
                                <div key={b.id} className="bg-slate-900 p-8 rounded-[2.5rem] border border-slate-800 hover:border-blue-500/40 transition-all group relative">
                  <span className="text-[10px] font-black bg-blue-500/10 text-blue-400 px-4 py-1.5 rounded-full uppercase tracking-tighter">
                    {b.categoria}
                  </span>
                                    <h4 className="text-2xl font-bold text-white mt-5 leading-tight">{b.titulo}</h4>
                                    <p className="text-slate-500 font-medium">{b.autor}</p>
                                    <div className="mt-10 pt-6 border-t border-slate-800/50 flex items-center justify-between">
                                        <span className="text-[10px] text-slate-600 font-mono">ISBN {b.isbn}</span>
                                        <TrendingUp size={18} className="text-slate-800" />
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {activeTab === 'google' && (
                    <div className="space-y-12 view-enter">
                        <header>
                            <h2 className="text-4xl font-bold text-white tracking-tight">Google Books</h2>
                            <p className="text-slate-500 mt-3 text-lg font-medium">Busque e importe obras externas.</p>
                        </header>

                        <div className="flex gap-4 p-2 bg-slate-900 border border-slate-800 rounded-[1.8rem] focus-within:border-blue-500/50 transition-all">
                            <div className="relative flex-1">
                                <Search className="absolute left-6 top-1/2 -translate-y-1/2 text-slate-600" size={24} />
                                <input
                                    className="w-full bg-transparent py-5 pl-16 pr-6 outline-none text-white text-lg"
                                    placeholder="Pesquisar..."
                                    value={searchTerm}
                                    onChange={e => setSearchTerm(e.target.value)}
                                    onKeyPress={e => e.key === 'Enter' && handleSearch()}
                                />
                            </div>
                            <button onClick={handleSearch} className="bg-blue-600 hover:bg-blue-500 text-white px-12 py-4 rounded-[1.4rem] font-black transition-all">
                                {loading ? "..." : "BUSCAR"}
                            </button>
                        </div>

                        <div className="grid grid-cols-1 gap-6">
                            {googleBooks.map((b, i) => (
                                <div key={i} className="bg-slate-900/60 p-8 rounded-2xl border border-slate-800 flex justify-between items-center group hover:bg-slate-800/30 transition-all">
                                    <div>
                                        <h5 className="font-bold text-white text-2xl leading-tight">{b.titulo}</h5>
                                        <p className="text-xs text-slate-500 mt-2 font-black uppercase">
                                            {b.autor} | {b.categoria}
                                        </p>
                                    </div>
                                    <button
                                        onClick={() => importBook(b)}
                                        className="flex items-center gap-4 text-xs font-black text-blue-400 hover:text-white bg-blue-400/5 hover:bg-blue-600 px-10 py-5 rounded-2xl transition-all border border-blue-400/10 hover:border-blue-400"
                                    >
                                        Importar <ArrowRight size={16} />
                                    </button>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {/* View de Recomendações */}
                {activeTab === 'recomendacoes' && (
                    <div className="space-y-10 view-enter">
                        <header className="flex justify-between items-end border-b border-slate-800 pb-8">
                            <div>
                                <h2 className="text-4xl font-bold text-white tracking-tight">
                                    Sugeridos para {selectedUser?.nome}
                                </h2>
                                <p className="text-slate-500 mt-2 font-medium text-lg italic">Motor de inteligência Elotech.</p>
                            </div>
                            <button onClick={() => setActiveTab('usuarios')} className="text-slate-400 hover:text-white text-sm font-bold border-b border-slate-800">Voltar</button>
                        </header>

                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {recommendations.map(b => (
                                <div key={b.id} className="bg-gradient-to-br from-slate-900 to-slate-800 p-8 rounded-[2.5rem] border border-blue-500/20 shadow-xl">
                                    <span className="text-[10px] font-black text-amber-400 uppercase tracking-widest">{b.categoria}</span>
                                    <h4 className="text-xl font-bold text-white mt-4 leading-tight">{b.titulo}</h4>
                                    <p className="text-slate-400 text-sm mt-1">{b.autor}</p>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </main>
        </div>
    );
}

function MenuBtn({ id, icon: Icon, label, active, setter }) {
    const isActive = active === id;
    return (
        <button
            onClick={() => setter(id)}
            className={`w-full flex items-center gap-4 px-5 py-4 rounded-2xl transition-all duration-300 ${
                isActive ? "bg-blue-600 text-white shadow-xl shadow-blue-900/40 font-bold" : "text-slate-500 hover:bg-slate-800/50 hover:text-slate-200"
            }`}
        >
            <Icon size={20} strokeWidth={isActive ? 2.5 : 2} />
            <span className="text-sm tracking-wide">{label}</span>
        </button>
    );
}

function StatCard({ title, value, icon: Icon, color }) {
    return (
        <div className="bg-slate-900 p-12 rounded-[3rem] border border-slate-800 flex justify-between items-center relative overflow-hidden group shadow-2xl">
            <div className="relative z-10">
                <p className="text-slate-500 text-[10px] font-black uppercase tracking-[0.4em] mb-4">{title}</p>
                <p className="text-7xl font-black text-white leading-none">{value}</p>
            </div>
            <Icon size={120} className={`${color} opacity-[0.03] absolute -right-6 -bottom-6 group-hover:scale-110 transition-all duration-1000`} />
        </div>
    );
}