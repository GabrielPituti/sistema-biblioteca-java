import React, { useState, useEffect, useCallback } from 'react';
import {
    Users, Book, Search, Library, LayoutDashboard, Globe,
    ArrowRight, BookOpen, AlertCircle, CheckCircle,
    Sparkles, RotateCcw, BookmarkPlus
} from 'lucide-react';

const API_BASE = "http://localhost:8080/api";

export default function App() {
    const [activeTab, setActiveTab] = useState('dashboard');
    const [users, setUsers] = useState([]);
    const [books, setBooks] = useState([]);
    const [loans, setLoans] = useState([]);
    const [googleBooks, setGoogleBooks] = useState([]);
    const [recommendations, setRecommendations] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [loading, setLoading] = useState(false);
    const [toast, setToast] = useState({ text: '', type: '' });

    const notify = useCallback((text, type = 'success') => {
        setToast({ text, type });
        setTimeout(() => setToast({ text: '', type: '' }), 5000);
    }, []);

    const loadData = useCallback(async () => {
        try {
            const [uRes, bRes, lRes] = await Promise.all([
                fetch(`${API_BASE}/usuarios`),
                fetch(`${API_BASE}/livros`),
                fetch(`${API_BASE}/emprestimos`).catch(() => ({ ok: false }))
            ]);
            if (uRes.ok) setUsers(await uRes.json());
            if (bRes.ok) setBooks(await bRes.json());
            if (lRes && lRes.ok) setLoans(await lRes.json());
        } catch (_err) {
            notify("Erro de sincronização com o servidor.", "error");
        }
    }, [notify]);

    useEffect(() => {
        loadData();
    }, [loadData]);

    const handleLoan = async (bookId, userId) => {
        if (!userId) { notify("Selecione um membro antes de prosseguir.", "error"); return; }
        try {
            const res = await fetch(`${API_BASE}/emprestimos`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ livroId: bookId, usuarioId: userId })
            });
            if (res.ok) {
                notify("Empréstimo registrado com sucesso!");
                setSelectedUser(null);
                await loadData();
                setActiveTab('locacoes');
            } else {
                const data = await res.json();
                notify(data.message || "Não foi possível realizar o empréstimo.", "error");
            }
        } catch (_err) { notify("Falha crítica no servidor.", "error"); }
    };

    const handleReturn = async (loanId) => {
        try {
            const res = await fetch(`${API_BASE}/emprestimos/${loanId}/devolver`, { method: 'PUT' });
            if (res.ok) {
                notify("Devolução processada.");
                await loadData();
            }
        } catch (_err) { notify("Erro ao processar devolução.", "error"); }
    };

    const handleSearch = async () => {
        if (!searchTerm.trim()) return;
        setLoading(true);
        try {
            const res = await fetch(`${API_BASE}/google-books/search?titulo=${encodeURIComponent(searchTerm)}`);
            if (res.ok) {
                setGoogleBooks(await res.json());
            } else {
                notify("Obras não encontradas.", "error");
            }
        } catch (_err) { notify("Erro na API externa.", "error"); }
        finally { setLoading(false); }
    };

    const handleImport = async (book) => {
        try {
            const res = await fetch(`${API_BASE}/livros`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(book)
            });
            if (res.ok) {
                notify(`"${book.titulo}" adicionado ao acervo.`);
                await loadData();
            }
        } catch (_err) { notify("Erro ao importar obra.", "error"); }
    };

    const fetchRecommendations = async (user) => {
        try {
            const res = await fetch(`${API_BASE}/emprestimos/recomendacoes/${user.id}`);
            if (res.ok) {
                setRecommendations(await res.json());
                setSelectedUser(user);
                setActiveTab('recomendacoes');
            }
        } catch (_err) { notify("Erro no motor de sugestões.", "error"); }
    };

    return (
        <div className="flex min-h-screen bg-[#020617] text-slate-100 font-sans">
            <aside className="w-72 bg-slate-900/50 border-r border-slate-800 p-8 flex flex-col h-screen sticky top-0 backdrop-blur-xl">
                <div className="flex items-center gap-3 text-indigo-500 mb-12">
                    <Library size={38} strokeWidth={2.5} />
                    <h1 className="text-xl font-bold text-white uppercase tracking-tighter leading-none italic">ELOTECH<br/>BIBLIOTECA</h1>
                </div>
                <nav className="space-y-3 flex-1">
                    <MenuBtn id="dashboard" icon={LayoutDashboard} label="Painel" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="usuarios" icon={Users} label="Membros" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="livros" icon={BookOpen} label="Acervo Local" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="locacoes" icon={BookmarkPlus} label="Locações" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="google" icon={Globe} label="Busca de Obras" active={activeTab} setter={setActiveTab} />
                </nav>
            </aside>

            <main className="flex-1 p-12 overflow-y-auto">
                {toast.text && (
                    <div className={`fixed top-8 right-8 p-5 rounded-2xl border shadow-2xl flex items-center gap-4 z-50 fade-in ${
                        toast.type === 'error' ? "bg-red-950 border-red-800" : "bg-indigo-950 border-indigo-800"
                    }`}>
                        {toast.type === 'error' ? <AlertCircle /> : <CheckCircle />}
                        <span className="text-sm font-bold">{toast.text}</span>
                    </div>
                )}

                {activeTab === 'dashboard' && (
                    <div className="space-y-12 fade-in">
                        <h2 className="text-6xl font-black text-white tracking-tighter leading-none">PAINEL</h2>
                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-10">
                            <StatCard title="Obras Cadastradas" value={books.length} icon={Book} color="text-indigo-500" />
                            <StatCard title="Membros Ativos" value={users.length} icon={Users} color="text-emerald-500" />
                        </div>
                    </div>
                )}

                {activeTab === 'usuarios' && (
                    <div className="space-y-8 fade-in">
                        <h2 className="text-4xl font-bold">Membros</h2>
                        <div className="bg-slate-900 border border-slate-800 rounded-[2rem] overflow-hidden shadow-2xl">
                            <table className="w-full text-left">
                                <thead className="bg-slate-800/40 text-slate-500 text-[10px] uppercase font-bold tracking-widest">
                                <tr><th className="px-10 py-6">Nome</th><th className="px-10 py-6 text-right">Ação</th></tr>
                                </thead>
                                <tbody className="divide-y divide-slate-800/50">
                                {users.map(u => (
                                    <tr key={u.id} className="hover:bg-slate-800/20">
                                        <td className="px-10 py-6 font-bold">{u.nome}</td>
                                        <td className="px-10 py-6 text-right flex gap-2 justify-end">
                                            <button onClick={() => { setSelectedUser(u); setActiveTab('livros'); }} className="text-indigo-400 text-[10px] font-black uppercase bg-indigo-500/10 px-4 py-2 rounded-xl hover:bg-indigo-600 hover:text-white transition-all">Emprestar</button>
                                            <button onClick={() => fetchRecommendations(u)} className="text-emerald-400 text-[10px] font-black uppercase bg-emerald-500/10 px-4 py-2 rounded-xl hover:bg-emerald-600 hover:text-white transition-all">Sugestões</button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}

                {activeTab === 'livros' && (
                    <div className="space-y-10 fade-in">
                        <div className="flex justify-between items-end border-b border-slate-800 pb-8">
                            <h2 className="text-4xl font-bold text-white">Acervo Local</h2>
                            {selectedUser && <div className="px-6 py-2 bg-indigo-600/20 rounded-xl text-indigo-400 text-xs font-bold animate-pulse uppercase tracking-widest">P/ {selectedUser.nome}</div>}
                        </div>
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                            {books.map(b => (
                                <div key={b.id} className="bg-slate-900 p-8 rounded-[2.5rem] border border-slate-800 hover:border-indigo-500/40 transition-all shadow-xl">
                                    <span className="text-[10px] font-black text-indigo-400 uppercase italic mb-4 block">{b.categoria}</span>
                                    <h4 className="text-2xl font-bold text-white leading-tight">{b.titulo}</h4>
                                    <p className="text-slate-500 text-sm mt-1">{b.autor}</p>
                                    <div className="mt-8 pt-6 border-t border-slate-800 flex justify-between items-center">
                                        <span className="text-[10px] font-mono text-slate-600">ISBN {b.isbn}</span>
                                        <button onClick={() => handleLoan(b.id, selectedUser?.id)} className="p-2 rounded-full bg-slate-800 text-slate-400 hover:bg-indigo-600 hover:text-white transition-all"><BookmarkPlus size={20} /></button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {activeTab === 'locacoes' && (
                    <div className="space-y-8 fade-in">
                        <h2 className="text-4xl font-bold text-white">Locações Ativas</h2>
                        <div className="grid grid-cols-1 gap-4">
                            {loans.filter(l => l.status === 'ATIVO').map(l => (
                                <div key={l.id} className="bg-slate-900/60 p-8 rounded-[2.5rem] border border-slate-800 flex justify-between items-center group shadow-xl">
                                    <div>
                                        <h5 className="font-bold text-white text-xl">{l.tituloLivro || "Livro #"+l.livroId}</h5>
                                        <p className="text-xs text-slate-500 uppercase tracking-widest font-black mt-1">Membro: {l.nomeUsuario || "ID #"+l.usuarioId}</p>
                                    </div>
                                    <button onClick={() => handleReturn(l.id)} className="flex items-center gap-3 text-xs font-black text-amber-400 hover:text-white bg-amber-400/10 px-8 py-4 rounded-2xl border border-amber-400/20 hover:bg-amber-600 transition-all"><RotateCcw size={16} /> DEVOLVER</button>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {activeTab === 'google' && (
                    <div className="space-y-12 fade-in">
                        <h2 className="text-4xl font-bold italic text-white uppercase tracking-tighter">Busca de Obras</h2>
                        <div className="flex gap-4 p-2 bg-slate-900 border border-slate-800 rounded-[1.8rem] focus-within:border-indigo-500/50 transition-all shadow-inner">
                            <div className="relative flex-1">
                                <Search className="absolute left-6 top-1/2 -translate-y-1/2 text-slate-600" size={26} />
                                <input className="w-full bg-transparent py-6 pl-16 pr-6 outline-none text-white text-xl placeholder-slate-700" placeholder="Digite título ou autor..." value={searchTerm} onChange={e => setSearchTerm(e.target.value)} onKeyPress={e => e.key === 'Enter' && handleSearch()} />
                            </div>
                            <button onClick={handleSearch} disabled={loading} className="bg-indigo-600 hover:bg-indigo-500 text-white px-12 py-5 rounded-[1.4rem] font-black transition-all shadow-xl disabled:opacity-50">{loading ? "..." : "BUSCAR"}</button>
                        </div>
                        <div className="grid grid-cols-1 gap-6">
                            {googleBooks.map((b, i) => (
                                <div key={i} className="bg-slate-900/60 p-8 rounded-[3rem] border border-slate-800 flex justify-between items-center shadow-xl">
                                    <div>
                                        <h5 className="font-bold text-white text-2xl leading-tight">{b.titulo}</h5>
                                        <p className="text-[10px] text-slate-500 font-black uppercase tracking-[0.2em] mt-2 italic">{b.autor} | {b.categoria}</p>
                                    </div>
                                    <button onClick={() => handleImport(b)} className="flex items-center gap-4 text-[10px] font-black text-indigo-400 hover:text-white border border-indigo-400/20 px-10 py-5 rounded-2xl hover:bg-indigo-600 uppercase tracking-widest">Importar <ArrowRight size={16} /></button>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {activeTab === 'recomendacoes' && (
                    <div className="space-y-12 fade-in">
                        <header className="flex justify-between items-end border-b border-slate-800 pb-10">
                            <div>
                                <h2 className="text-5xl font-black text-white tracking-tighter flex items-center gap-5 leading-none uppercase">SUGESTÕES: {selectedUser?.nome} <Sparkles className="text-amber-400" /></h2>
                                <p className="text-slate-500 mt-4 text-xl font-medium italic">Análise de afinidade Elotech.</p>
                            </div>
                            <button onClick={() => setActiveTab('usuarios')} className="text-slate-400 hover:text-white text-sm font-bold border-b border-slate-800 pb-1 transition-colors">Voltar</button>
                        </header>
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                            {recommendations.map(b => (
                                <div key={b.id} className="bg-linear-to-br from-slate-900 to-slate-800 p-8 rounded-[2.5rem] border border-blue-500/20 shadow-2xl group flex flex-col">
                                    <span className="text-[10px] font-black text-amber-400 uppercase tracking-widest italic mb-4 block">{b.categoria}</span>
                                    <h4 className="text-xl font-bold text-white mt-auto leading-tight">{b.titulo}</h4>
                                    <p className="text-slate-400 text-sm mt-1 mb-6">{b.autor}</p>
                                    <button onClick={() => handleLoan(b.id, selectedUser.id)} className="mt-auto w-full py-4 bg-indigo-600 text-white rounded-2xl font-black text-[10px] uppercase hover:bg-indigo-500 transition-all shadow-xl tracking-widest">Reservar</button>
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
        <button onClick={() => setter(id)} className={`w-full flex items-center gap-4 px-5 py-5 rounded-2xl transition-all duration-300 ${isActive ? "bg-indigo-600 text-white shadow-xl shadow-indigo-900/40 font-bold" : "text-slate-500 hover:bg-slate-800/50 hover:text-slate-200"}`}>
            <Icon size={20} strokeWidth={isActive ? 2.5 : 2} /> <span className="text-sm tracking-wide">{label}</span>
        </button>
    );
}

function StatCard({ title, value, icon: Icon, color }) {
    return (
        <div className="bg-slate-900 p-12 rounded-[3.5rem] border border-slate-800 flex justify-between items-center group shadow-2xl relative overflow-hidden">
            <div className="relative z-10"><p className="text-slate-500 text-[10px] font-black uppercase mb-4 tracking-widest">{title}</p><p className="text-8xl font-black text-white leading-none tabular-nums">{value}</p></div>
            <Icon size={160} className={`${color} opacity-[0.03] absolute -right-10 -bottom-10 group-hover:scale-110 transition-all duration-1000`} />
        </div>
    );
}