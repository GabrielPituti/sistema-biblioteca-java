import React, { useState, useEffect, useCallback } from 'react';
import {
    Users, Book, Search, Library, Plus,
    LayoutDashboard, Globe, ArrowRight,
    TrendingUp, BookOpen, Trash2, AlertCircle, CheckCircle,
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

        } catch (err) {
            notify("Erro ao sincronizar dados. Verifique o backend.", "error");
        }
    }, []);

    useEffect(() => {
        loadData();
    }, [loadData]);

    const notify = (text, type = 'success') => {
        setToast({ text, type });
        setTimeout(() => setToast({ text: '', type: '' }), 5000);
    };

    const handleLoan = async (bookId, userId) => {
        if (!userId) {
            notify("Selecione um membro na aba Membros primeiro.", "error");
            return;
        }
        try {
            const res = await fetch(`${API_BASE}/emprestimos`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ livroId: bookId, usuarioId: userId })
            });
            if (res.ok) {
                notify("Empréstimo realizado com sucesso!");
                setSelectedUser(null);
                loadData();
                setActiveTab('locacoes');
            } else {
                const err = await res.json();
                notify(err.message || "Livro indisponível.", "error");
            }
        } catch (err) {
            notify("Falha no servidor de empréstimos.", "error");
        }
    };

    const handleReturn = async (loanId) => {
        try {
            const res = await fetch(`${API_BASE}/emprestimos/${loanId}/devolver`, { method: 'PUT' });
            if (res.ok) {
                notify("Devolução registrada no acervo.");
                loadData();
            }
        } catch (err) {
            notify("Erro ao processar devolução.", "error");
        }
    };

    const handleSearch = async () => {
        if (!searchTerm) return;
        setLoading(true);
        try {
            const res = await fetch(`${API_BASE}/google-books/search?titulo=${searchTerm}`);
            const data = await res.json();
            setGoogleBooks(data);
        } catch (err) {
            notify("Erro na busca do Google Books.", "error");
        } finally {
            setLoading(false);
        }
    };

    const handleImport = async (book) => {
        try {
            const res = await fetch(`${API_BASE}/livros`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(book)
            });
            if (res.ok) {
                notify("Obra importada com sucesso!");
                loadData();
            }
        } catch (err) {
            notify("Erro ao salvar livro importado.", "error");
        }
    };

    const showRecommendations = async (user) => {
        try {
            const res = await fetch(`${API_BASE}/emprestimos/recomendacoes/${user.id}`);
            if (res.ok) {
                setRecommendations(await res.json());
                setSelectedUser(user);
                setActiveTab('recomendacoes');
            }
        } catch (err) {
            notify("Erro ao gerar análise de perfil.", "error");
        }
    };

    return (
        <div className="flex min-h-screen bg-slate-950 text-slate-100 font-sans antialiased">

            {/* Sidebar */}
            <aside className="w-72 bg-slate-900/50 border-r border-slate-800 p-8 flex flex-col h-screen sticky top-0 backdrop-blur-xl">
                <div className="flex items-center gap-3 text-blue-500 mb-12 px-2 font-bold">
                    <Library size={38} strokeWidth={2.5} />
                    <h1 className="text-2xl font-black tracking-tighter text-white uppercase italic">ELOTECH</h1>
                </div>

                <nav className="space-y-3 flex-1">
                    <MenuBtn id="dashboard" icon={LayoutDashboard} label="Dashboard" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="usuarios" icon={Users} label="Membros" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="livros" icon={BookOpen} label="Acervo Local" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="locacoes" icon={BookmarkPlus} label="Locações" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="google" icon={Globe} label="Busca Mundial" active={activeTab} setter={setActiveTab} />
                </nav>

                <div className="pt-6 border-t border-slate-800 text-[10px] text-slate-600 font-bold uppercase tracking-[0.4em] text-center">
                    Desafio 2026
                </div>
            </aside>

            {/* Área de Conteúdo */}
            <main className="flex-1 p-12 overflow-y-auto relative">

                {toast.text && (
                    <div className={`fixed top-8 right-8 p-5 rounded-2xl border shadow-2xl flex items-center gap-4 z-50 fade-in ${
                        toast.type === 'error' ? "bg-red-950 border-red-800" : "bg-blue-950 border-blue-800"
                    }`}>
                        {toast.type === 'error' ? <AlertCircle className="text-red-500" /> : <CheckCircle className="text-blue-500" />}
                        <span className="text-sm font-bold">{toast.text}</span>
                    </div>
                )}

                {activeTab === 'dashboard' && (
                    <div className="space-y-12 fade-in">
                        <header>
                            <h2 className="text-6xl font-black text-white tracking-tighter leading-none">Resumo</h2>
                            <p className="text-slate-500 mt-4 text-xl font-medium max-w-xl italic">
                                Indicadores operacionais em tempo real.
                            </p>
                        </header>
                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-10">
                            <StatCard title="Livros no Catálogo" value={books.length} icon={Book} color="text-blue-500" />
                            <StatCard title="Usuários Registrados" value={users.length} icon={Users} color="text-emerald-500" />
                        </div>
                    </div>
                )}

                {activeTab === 'usuarios' && (
                    <div className="space-y-8 fade-in">
                        <h2 className="text-4xl font-bold text-white tracking-tight">Gestão de Membros</h2>
                        <div className="bg-slate-900 border border-slate-800 rounded-[2.5rem] overflow-hidden shadow-2xl">
                            <table className="w-full text-left">
                                <thead className="bg-slate-800/40 text-slate-500 text-[10px] font-bold uppercase tracking-[0.4em]">
                                <tr><th className="px-10 py-6">Membro</th><th className="px-10 py-6">E-mail</th><th className="px-10 py-6 text-right">Inteligência</th></tr>
                                </thead>
                                <tbody className="divide-y divide-slate-800/50">
                                {users.map(u => (
                                    <tr key={u.id} className="hover:bg-slate-800/30 transition-colors group">
                                        <td className="px-10 py-6 font-bold text-slate-200">{u.nome}</td>
                                        <td className="px-10 py-6 text-slate-400">{u.email}</td>
                                        <td className="px-10 py-6 text-right flex gap-2 justify-end">
                                            <button onClick={() => { setSelectedUser(u); setActiveTab('livros'); }} className="text-indigo-400 text-[10px] font-black uppercase tracking-widest bg-indigo-500/10 px-4 py-2 rounded-xl hover:bg-indigo-600 hover:text-white transition-all">Emprestar</button>
                                            <button onClick={() => showRecommendations(u)} className="text-blue-400 text-[10px] font-black uppercase tracking-widest bg-blue-500/10 px-4 py-2 rounded-xl hover:bg-blue-600 hover:text-white transition-all">Sugerir</button>
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
                        <div className="flex justify-between items-end">
                            <h2 className="text-4xl font-bold text-white tracking-tight">Acervo Local</h2>
                            {selectedUser && (
                                <div className="px-6 py-2 bg-indigo-600/20 border border-indigo-600/30 rounded-xl text-indigo-400 text-xs font-bold animate-pulse">
                                    SELECIONADO: {selectedUser.nome}
                                </div>
                            )}
                        </div>
                        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-8">
                            {books.map(b => (
                                <div key={b.id} className="bg-slate-900 p-8 rounded-[2.5rem] border border-slate-800 hover:border-blue-500/40 transition-all group relative overflow-hidden shadow-2xl">
                                    <div className="flex justify-between items-start mb-6">
                                        <span className="text-[10px] font-black bg-blue-500/10 text-blue-400 px-4 py-1.5 rounded-full uppercase tracking-tighter italic">{b.categoria}</span>
                                        <TrendingUp size={18} className="text-slate-800" />
                                    </div>
                                    <h4 className="text-2xl font-bold text-white leading-tight mb-2">{b.titulo}</h4>
                                    <p className="text-slate-500 font-medium text-sm">{b.autor}</p>
                                    <div className="mt-10 pt-6 border-t border-slate-800/50 flex items-center justify-between">
                                        <span className="text-[9px] text-slate-600 font-mono font-bold">ISBN {b.isbn}</span>
                                        <button
                                            onClick={() => handleLoan(b.id, selectedUser?.id)}
                                            className="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center text-slate-500 hover:bg-blue-600 hover:text-white transition-all"
                                        >
                                            <BookmarkPlus size={18} />
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {activeTab === 'locacoes' && (
                    <div className="space-y-8 fade-in">
                        <h2 className="text-4xl font-bold text-white tracking-tight leading-none">Locações</h2>
                        <div className="grid grid-cols-1 gap-4">
                            {loans.filter(l => l.status === 'ATIVO').map(l => (
                                <div key={l.id} className="bg-slate-900/60 p-8 rounded-[2.5rem] border border-slate-800 flex justify-between items-center group shadow-xl hover:bg-slate-900/80 transition-all">
                                    <div>
                                        <h5 className="font-bold text-white text-xl leading-tight">{l.tituloLivro || "Livro #"+l.livroId}</h5>
                                        <p className="text-xs text-slate-500 mt-2 font-black uppercase tracking-widest flex items-center gap-3">
                                            Membro: {l.nomeUsuario || "ID #"+l.usuarioId}
                                        </p>
                                    </div>
                                    <button onClick={() => handleReturn(l.id)} className="flex items-center gap-3 text-xs font-black text-amber-400 hover:text-white bg-amber-400/5 hover:bg-amber-600 px-10 py-5 rounded-2xl transition-all border border-amber-400/10 hover:border-amber-400 uppercase tracking-widest shadow-sm">
                                        <RotateCcw size={16} /> Devolver
                                    </button>
                                </div>
                            ))}
                            {loans.filter(l => l.status === 'ATIVO').length === 0 && <div className="p-20 text-center text-slate-800 font-black text-2xl uppercase tracking-widest opacity-20">Nenhuma locação ativa</div>}
                        </div>
                    </div>
                )}

                {activeTab === 'google' && (
                    <div className="space-y-12 fade-in">
                        <header>
                            <h2 className="text-4xl font-bold text-white tracking-tight italic leading-none">Busca Mundial</h2>
                            <p className="text-slate-500 mt-4 text-lg font-medium">Explore o catálogo da Google e importe novos exemplares.</p>
                        </header>
                        <div className="flex gap-4 p-2 bg-slate-900 border border-slate-800 rounded-[1.8rem] focus-within:border-blue-500/50 transition-all shadow-inner">
                            <div className="relative flex-1">
                                <Search className="absolute left-6 top-1/2 -translate-y-1/2 text-slate-600" size={24} />
                                <input className="w-full bg-transparent py-5 pl-16 pr-6 outline-none text-white text-lg placeholder-slate-700" placeholder="Digite título, autor ou tecnologia..." value={searchTerm} onChange={e => setSearchTerm(e.target.value)} onKeyPress={e => e.key === 'Enter' && handleSearch()} />
                            </div>
                            <button onClick={handleSearch} className="bg-blue-600 hover:bg-blue-500 text-white px-12 py-4 rounded-[1.4rem] font-black transition-all shadow-lg active:scale-95 disabled:opacity-50" disabled={loading}>
                                {loading ? "..." : "BUSCAR"}
                            </button>
                        </div>
                        <div className="grid grid-cols-1 gap-6">
                            {googleBooks.map((b, i) => (
                                <div key={i} className="bg-slate-900/60 p-8 rounded-[3rem] border border-slate-800 flex flex-col md:flex-row justify-between items-start md:items-center gap-6 group hover:bg-slate-800/30 transition-all shadow-xl">
                                    <div>
                                        <h5 className="font-bold text-white text-2xl leading-tight">{b.titulo}</h5>
                                        <p className="text-[10px] text-slate-500 mt-2 font-black uppercase tracking-[0.2em] flex items-center gap-3 italic">{b.autor} <span className="w-1 h-1 rounded-full bg-slate-800"></span> {b.categoria}</p>
                                    </div>
                                    <button onClick={() => handleImport(b)} className="flex items-center gap-4 text-xs font-black text-blue-400 hover:text-white bg-blue-400/5 hover:bg-blue-600 px-10 py-5 rounded-2xl transition-all border border-blue-400/10 hover:border-blue-400 uppercase tracking-widest shadow-sm">Importar <ArrowRight size={16} /></button>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {activeTab === 'recomendacoes' && (
                    <div className="space-y-12 fade-in">
                        <header className="flex justify-between items-end border-b border-slate-800 pb-10">
                            <div>
                                <h2 className="text-5xl font-black text-white tracking-tighter flex items-center gap-5 leading-none">Match de Leitura: {selectedUser?.nome} <Sparkles className="text-amber-400" /></h2>
                                <p className="text-slate-500 mt-4 text-xl font-medium italic leading-relaxed text-balance">Sugestões exclusivas baseadas no perfil de consumo literário do membro.</p>
                            </div>
                            <button onClick={() => setActiveTab('usuarios')} className="text-slate-400 hover:text-white text-sm font-bold border-b border-slate-800 pb-1 transition-colors">Voltar</button>
                        </header>
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                            {recommendations.map(b => (
                                <div key={b.id} className="bg-gradient-to-br from-slate-900 to-slate-800 p-8 rounded-[2.5rem] border border-blue-500/20 shadow-2xl relative overflow-hidden group">
                                    <span className="text-[10px] font-black text-amber-400 uppercase tracking-widest italic">{b.categoria}</span>
                                    <h4 className="text-xl font-bold text-white mt-5 leading-tight">{b.titulo}</h4>
                                    <p className="text-slate-400 text-sm mt-1">{b.autor}</p>
                                    <button onClick={() => handleLoan(b.id, selectedUser.id)} className="mt-8 w-full py-4 bg-blue-600 text-white rounded-2xl font-black text-[10px] uppercase tracking-widest hover:bg-blue-500 transition-all shadow-xl">Solicitar Empréstimo</button>
                                </div>
                            ))}
                            {recommendations.length === 0 && <div className="col-span-full p-20 text-center text-slate-800 font-bold text-2xl uppercase tracking-widest opacity-20">Nenhuma afinidade identificada</div>}
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
        <button onClick={() => setter(id)} className={`w-full flex items-center gap-4 px-5 py-5 rounded-2xl transition-all duration-300 ${
            isActive ? "bg-blue-600 text-white shadow-xl shadow-blue-900/40 font-bold scale-[1.02]" : "text-slate-500 hover:bg-slate-800/50 hover:text-slate-200"
        }`}>
            <Icon size={20} strokeWidth={isActive ? 2.5 : 2} /> <span className="text-sm tracking-wide">{label}</span>
        </button>
    );
}

function StatCard({ title, value, icon: Icon, color }) {
    return (
        <div className="bg-slate-900 p-12 rounded-[3.5rem] border border-slate-800 flex justify-between items-center relative overflow-hidden group shadow-2xl shadow-black/40">
            <div className="relative z-10"><p className="text-slate-500 text-[10px] font-black uppercase tracking-[0.5em] mb-5">{title}</p><p className="text-8xl font-black text-white leading-none">{value}</p></div>
            <Icon size={160} className={`${color} opacity-[0.03] absolute -right-10 -bottom-10 group-hover:scale-110 transition-all duration-1000`} />
        </div>
    );
}