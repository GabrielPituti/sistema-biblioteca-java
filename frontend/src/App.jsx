import React, { useState, useEffect, useCallback } from 'react';
import {
    Users, Book, Search, Library, LayoutDashboard, Globe,
    BookOpen, AlertCircle, CheckCircle,
    Sparkles, RotateCcw, BookmarkPlus, ArrowRight, UserCheck, Check,
    LibraryBig
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
    const [importedIsbns, setImportedIsbns] = useState(new Set());

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
        } catch {
            notify("Erro de sincronização com o servidor.", "error");
        }
    }, [notify]);

    useEffect(() => { void loadData(); }, [loadData]);

    const handleReturn = async (loanId) => {
        try {
            const res = await fetch(`${API_BASE}/emprestimos/${loanId}/devolver`, { method: 'PUT' });
            if (res.ok) {
                notify("Devolução concluída.");
                void loadData();
            } else {
                const data = await res.json().catch(() => ({}));
                notify(data.message || "Erro ao processar devolução.", "error");
            }
        } catch { notify("Falha na comunicação.", "error"); }
    };

    const handleImport = async (book) => {
        const sanitized = {
            ...book,
            isbn: (book.isbn === "N/A" || !book.isbn) ? `ID-${Date.now()}` : book.isbn,
            dataPublicacao: book.dataPublicacao || new Date().toISOString().split('T')[0]
        };

        try {
            const res = await fetch(`${API_BASE}/livros`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(sanitized)
            });
            if (res.ok) {
                notify("Obra importada com sucesso!");
                setImportedIsbns(prev => new Set(prev).add(book.isbn));
                void loadData();
            } else {
                const data = await res.json().catch(() => ({}));
                notify(data.message || "Erro na validação.", "error");
            }
        } catch { notify("Erro ao importar obra.", "error"); }
    };

    const handleLoan = async (bookId) => {
        if (!selectedUser) {
            notify("Selecione um membro primeiro.", "error");
            setActiveTab('usuarios');
            return;
        }
        try {
            const res = await fetch(`${API_BASE}/emprestimos`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ livroId: bookId, usuarioId: selectedUser.id })
            });
            if (res.ok) {
                notify("Empréstimo registrado!");
                setSelectedUser(null);
                void loadData();
                setActiveTab('locacoes');
            } else {
                const data = await res.json();
                notify(data.message || "Falha na operação.", "error");
            }
        } catch { notify("Erro de rede.", "error"); }
    };

    const handleSearch = async () => {
        if (!searchTerm.trim()) return;
        setLoading(true);
        try {
            const res = await fetch(`${API_BASE}/google-books/search?titulo=${encodeURIComponent(searchTerm)}`);
            if (res.ok) setGoogleBooks(await res.json());
            else notify(res.status === 429 ? "Cota de busca excedida." : "Erro na busca.", "error");
        } catch { notify("Falha na integração externa.", "error"); }
        finally { setLoading(false); }
    };

    const startRecommendations = async (user) => {
        try {
            const res = await fetch(`${API_BASE}/emprestimos/recomendacoes/${user.id}`);
            if (res.ok) {
                setRecommendations(await res.json());
                setSelectedUser(user);
                setActiveTab('recomendacoes');
            } else {
                notify("Não foi possível carregar as sugestões.", "error");
            }
        } catch { notify("Erro no motor de sugestões.", "error"); }
    };

    return (
        <div className="flex min-h-screen bg-[#020617] text-slate-100 font-sans antialiased">
            <aside className="w-72 bg-slate-900/50 border-r border-slate-800 p-8 flex flex-col h-screen sticky top-0 backdrop-blur-xl">
                <div className="flex items-center gap-3 text-indigo-500 mb-12">
                    <Library size={38} />
                    <h1 className="text-xl font-bold text-white uppercase italic tracking-tighter leading-none">ELOTECH<br/>BIBLIOTECA</h1>
                </div>
                <nav className="space-y-3 flex-1">
                    <MenuBtn id="dashboard" icon={LayoutDashboard} label="Painel" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="usuarios" icon={Users} label="Membros" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="livros" icon={BookOpen} label="Acervo Local" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="locacoes" icon={BookmarkPlus} label="Empréstimos" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="google" icon={Globe} label="Explorar Catálogo" active={activeTab} setter={setActiveTab} />
                </nav>

                {selectedUser && (
                    <div className="mt-auto p-4 bg-indigo-600/20 border border-indigo-500/40 rounded-2xl animate-pulse">
                        <div className="flex items-center gap-2 text-indigo-400 mb-1">
                            <UserCheck size={14} />
                            <p className="text-[10px] uppercase font-black tracking-widest">Locando para:</p>
                        </div>
                        <p className="text-sm font-bold text-white truncate">{selectedUser.nome}</p>
                        <button onClick={() => setSelectedUser(null)} className="text-[10px] text-slate-500 underline mt-2 hover:text-slate-300">Cancelar</button>
                    </div>
                )}
            </aside>

            <main className="flex-1 p-12 overflow-y-auto relative">
                {toast.text && <Toast message={toast.text} type={toast.type} />}

                {activeTab === 'dashboard' && <DashboardView books={books} users={users} />}

                {activeTab === 'usuarios' && (
                    <div className="space-y-8 fade-in">
                        <h2 className="text-4xl font-bold uppercase">Membros</h2>
                        <UserTable users={users} onSelect={setSelectedUser} onSuggest={startRecommendations} changeTab={setActiveTab} />
                    </div>
                )}

                {activeTab === 'livros' && (
                    <div className="space-y-10 fade-in">
                        <header className="flex justify-between items-end border-b border-slate-800 pb-8">
                            <h2 className="text-4xl font-bold uppercase">Acervo Local</h2>
                            {selectedUser && <div className="px-6 py-2 bg-indigo-600/20 rounded-xl text-indigo-400 text-xs font-bold animate-pulse uppercase tracking-widest italic">Selecione para {selectedUser.nome}</div>}
                        </header>
                        <BookGrid books={books} onLoan={handleLoan} hasSelectedUser={!!selectedUser} />
                    </div>
                )}

                {activeTab === 'locacoes' && (
                    <div className="space-y-8 fade-in">
                        <h2 className="text-4xl font-bold uppercase">Empréstimos Ativos</h2>
                        <LoanList loans={loans} onReturn={handleReturn} />
                    </div>
                )}

                {activeTab === 'google' && (
                    <div className="space-y-12 fade-in">
                        <h2 className="text-4xl font-bold italic uppercase tracking-tighter text-white">Descobrir Obras</h2>
                        <div className="flex gap-4 p-2 bg-slate-900 border border-slate-800 rounded-3xl shadow-inner">
                            <div className="relative flex-1">
                                <Search className="absolute left-6 top-1/2 -translate-y-1/2 text-slate-600" size={26} />
                                <input
                                    className="w-full bg-transparent py-6 pl-16 pr-6 outline-none text-white text-xl placeholder-slate-700"
                                    placeholder="Pesquisar catálogo global do Google..."
                                    value={searchTerm}
                                    onChange={e => setSearchTerm(e.target.value)}
                                    onKeyDown={e => e.key === 'Enter' && void handleSearch()}
                                />
                            </div>
                            <button onClick={handleSearch} disabled={loading} className="bg-indigo-600 hover:bg-indigo-500 text-white px-12 py-5 rounded-2xl font-black transition-all cursor-pointer disabled:opacity-50">
                                {loading ? "..." : "BUSCAR"}
                            </button>
                        </div>
                        <GoogleGrid books={googleBooks} onImport={handleImport} importedIsbns={importedIsbns} localBooks={books} />
                    </div>
                )}

                {activeTab === 'recomendacoes' && (
                    <div className="space-y-12 fade-in">
                        <header className="flex justify-between items-end border-b border-slate-800 pb-10">
                            <div>
                                <h2 className="text-5xl font-black text-white tracking-tighter flex items-center gap-5 leading-none uppercase">Sugestões: {selectedUser?.nome} <Sparkles className="text-amber-400" /></h2>
                                <p className="text-slate-500 mt-4 text-xl font-medium italic">Análise de afinidade baseada no histórico de leitura.</p>
                            </div>
                            <button onClick={() => setActiveTab('usuarios')} className="text-slate-400 hover:text-white text-sm font-bold border-b border-slate-800 pb-1 transition-colors cursor-pointer">Voltar</button>
                        </header>
                        <RecommendationGrid
                            list={recommendations}
                            onLoan={handleLoan}
                            userHasHistory={loans.some(l => l.nomeUsuario === selectedUser?.nome)}
                        />
                    </div>
                )}
            </main>
        </div>
    );
}

// componentes auxiliares

function MenuBtn(props) {
    const isActive = props.active === props.id;
    const Icon = props.icon;
    return (
        <button onClick={() => props.setter(props.id)} className={`w-full flex items-center gap-4 px-5 py-5 rounded-2xl transition-all duration-300 cursor-pointer ${isActive ? "bg-indigo-600 text-white shadow-xl shadow-indigo-900/40 font-bold scale-[1.02]" : "text-slate-500 hover:bg-slate-800/50 hover:text-slate-200"}`}>
            <Icon size={20} /> <span className="text-sm tracking-wide">{props.label}</span>
        </button>
    );
}

function StatCard(props) {
    const Icon = props.icon;
    return (
        <div className="bg-slate-900 p-12 rounded-4xl border border-slate-800 flex justify-between items-center group shadow-2xl relative overflow-hidden">
            <div className="relative z-10">
                <p className="text-slate-500 text-[10px] font-black uppercase mb-4 tracking-widest">{props.title}</p>
                <p className="text-8xl font-black text-white leading-none tabular-nums">{props.value}</p>
            </div>
            <Icon size={160} className={`${props.color} opacity-[0.03] absolute -right-10 -bottom-10 group-hover:scale-110 transition-all duration-1000`} />
        </div>
    );
}

function Toast({ message, type }) {
    const isError = type === 'error';
    return (
        <div className={`fixed top-8 right-8 p-5 rounded-2xl border shadow-2xl flex items-center gap-4 z-50 fade-in ${
            isError ? "bg-red-950 border-red-800 text-red-200" : "bg-indigo-950 border-indigo-800 text-indigo-200"
        }`}>
            {isError ? <AlertCircle size={24} /> : <CheckCircle size={24} />}
            <span className="text-sm font-bold tracking-tight">{message}</span>
        </div>
    );
}

function DashboardView({ books, users }) {
    return (
        <div className="space-y-12 fade-in">
            <h2 className="text-6xl font-black text-white tracking-tighter uppercase">Painel</h2>
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-10">
                <StatCard title="Livros no Acervo" value={books.length} icon={Book} color="text-indigo-500" />
                <StatCard title="Membros Registrados" value={users.length} icon={Users} color="text-emerald-500" />
            </div>
        </div>
    );
}

function UserTable({ users, onSelect, onSuggest, changeTab }) {
    return (
        <div className="bg-slate-900 border border-slate-800 rounded-4xl overflow-hidden shadow-2xl">
            <table className="w-full text-left">
                <thead className="bg-slate-800/40 text-slate-500 text-[10px] uppercase font-bold tracking-widest">
                <tr><th className="px-10 py-6">Nome</th><th className="px-10 py-6 text-right">Ação</th></tr>
                </thead>
                <tbody className="divide-y divide-slate-800/50">
                {users.map(u => (
                    <tr key={u.id} className="hover:bg-slate-800/20 transition-colors">
                        <td className="px-10 py-6 font-bold">{u.nome}</td>
                        <td className="px-10 py-6 text-right flex gap-3 justify-end">
                            <button onClick={() => { onSelect(u); changeTab('livros'); }} className="text-indigo-400 text-[10px] font-black uppercase bg-indigo-500/10 px-4 py-2.5 rounded-xl hover:bg-indigo-600 hover:text-white transition-all cursor-pointer">Locar Obra</button>
                            <button onClick={() => onSuggest(u)} className="text-emerald-400 text-[10px] font-black uppercase bg-emerald-500/10 px-4 py-2.5 rounded-xl hover:bg-emerald-600 hover:text-white transition-all cursor-pointer">Sugestões</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

function BookGrid({ books, onLoan, hasSelectedUser }) {
    return (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-8">
            {books.map(b => (
                <div key={b.id} className="bg-slate-900 p-8 rounded-4xl border border-slate-800 hover:border-indigo-500/40 transition-all shadow-xl group">
                    <span className="text-[10px] font-black bg-indigo-500/10 text-indigo-400 px-4 py-1.5 rounded-full uppercase tracking-tighter italic mb-4 block w-fit">{b.categoria}</span>
                    <h4 className="text-2xl font-bold text-white leading-tight group-hover:text-indigo-400 transition-colors">{b.titulo}</h4>
                    <p className="text-slate-500 font-medium text-sm">{b.autor}</p>
                    <div className="mt-10 pt-6 border-t border-slate-800/50 flex items-center justify-between">
                        <span className="text-[9px] text-slate-600 font-mono font-bold tracking-widest uppercase">ISBN {b.isbn}</span>
                        <button
                            onClick={() => onLoan(b.id)}
                            className={`w-10 h-10 rounded-full flex items-center justify-center transition-all cursor-pointer shadow-md ${hasSelectedUser ? "bg-indigo-600 text-white hover:bg-indigo-500 scale-110" : "bg-slate-800 text-slate-500"}`}
                        >
                            <BookmarkPlus size={20} />
                        </button>
                    </div>
                </div>
            ))}
        </div>
    );
}

function LoanList({ loans, onReturn }) {
    const activeLoans = loans.filter(l => l.status === 'ATIVO');
    if (activeLoans.length === 0) return <div className="p-20 text-center text-slate-800 font-black text-xl uppercase opacity-20 tracking-[0.3em]">Sem pendências</div>;
    return (
        <div className="grid grid-cols-1 gap-4">
            {activeLoans.map(l => (
                <div key={l.id} className="bg-slate-900/60 p-8 rounded-4xl border border-slate-800 flex justify-between items-center group shadow-xl hover:bg-slate-900/80 transition-all">
                    <div>
                        <h5 className="font-bold text-white text-xl leading-tight">{l.tituloLivro}</h5>
                        <p className="text-xs text-slate-500 mt-2 font-black uppercase tracking-widest italic">Portador: {l.nomeUsuario}</p>
                    </div>
                    <button onClick={() => onReturn(l.id)} className="flex items-center gap-4 text-[10px] font-black text-amber-400 hover:text-white bg-amber-400/5 hover:bg-amber-600 px-10 py-5 rounded-2xl transition-all border border-amber-400/10 uppercase tracking-widest cursor-pointer shadow-md"><RotateCcw size={16} /> DEVOLVER</button>
                </div>
            ))}
        </div>
    );
}

function GoogleGrid({ books, onImport, importedIsbns, localBooks }) {
    return (
        <div className="grid grid-cols-1 gap-6">
            {books.map((b, i) => {
                const isAlreadyImported = importedIsbns.has(b.isbn) || localBooks.some(local => local.isbn === b.isbn);
                return (
                    <div key={i} className="bg-slate-900/60 p-8 rounded-4xl border border-slate-800 flex flex-col md:flex-row justify-between items-start md:items-center gap-6 group hover:bg-slate-800/40 transition-all shadow-xl">
                        <div>
                            <h5 className="font-bold text-white text-2xl leading-tight">{b.titulo}</h5>
                            <p className="text-[10px] text-slate-500 font-black uppercase tracking-[0.2em] mt-2 italic">{b.autor} | {b.categoria}</p>
                        </div>
                        <button
                            onClick={() => onImport(b)}
                            disabled={isAlreadyImported}
                            className={`flex items-center gap-4 text-[10px] font-black px-10 py-5 rounded-2xl uppercase tracking-widest transition-all ${
                                isAlreadyImported
                                    ? "bg-slate-800 text-slate-500 cursor-not-allowed border border-slate-700"
                                    : "text-indigo-400 hover:text-white border border-indigo-400/20 hover:bg-indigo-600 cursor-pointer shadow-sm"
                            }`}
                        >
                            {isAlreadyImported ? <><Check size={16} /> Já no Acervo</> : <>Importar Agora <ArrowRight size={16} /></>}
                        </button>
                    </div>
                );
            })}
        </div>
    );
}

function RecommendationGrid({ list, onLoan, userHasHistory }) {
    if (list.length === 0) {
        return (
            <div className="col-span-full p-20 text-center bg-slate-900/40 rounded-4xl border border-dashed border-slate-800 flex flex-col items-center gap-4">
                <LibraryBig size={48} className="text-slate-700" />
                <div>
                    <p className="text-slate-400 font-bold uppercase tracking-[0.2em]">
                        {userHasHistory
                            ? "Você já explorou todas as obras destas categorias!"
                            : "Sem histórico de locações para este membro."}
                    </p>
                    <p className="text-slate-600 text-sm mt-2">
                        {userHasHistory
                            ? "Que tal descobrir novos gêneros em nossa busca global?"
                            : "Realize o primeiro empréstimo para ativar as sugestões por afinidade."}
                    </p>
                </div>
            </div>
        );
    }
    return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {list.map(b => (
                <div key={b.id} className="bg-linear-to-br from-slate-900 to-slate-800 p-8 rounded-4xl border border-blue-500/20 shadow-2xl group flex flex-col">
                    <span className="text-[10px] font-black text-amber-400 uppercase tracking-widest italic mb-4 block">{b.categoria}</span>
                    <h4 className="text-xl font-bold text-white mt-auto leading-tight">{b.titulo}</h4>
                    <p className="text-slate-400 text-sm mt-1 mb-6">{b.autor}</p>
                    <button onClick={() => onLoan(b.id)} className="mt-auto w-full py-4 bg-indigo-600 text-white rounded-2xl font-black text-[10px] uppercase hover:bg-indigo-500 transition-all shadow-xl tracking-widest cursor-pointer">Locar Agora</button>
                </div>
            ))}
        </div>
    );
}