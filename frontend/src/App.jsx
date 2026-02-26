import React, { useState, useEffect } from 'react';
import {
    Users, Book, Search, Library, Plus,
    LayoutDashboard, Globe, ArrowRight,
    TrendingUp, BookOpen, Trash2, AlertCircle, CheckCircle,
    Sparkles, Check, Mail
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
        loadInitialData();
    }, []);

    const loadInitialData = async () => {
        try {
            const [uRes, bRes] = await Promise.all([
                fetch(`${API_BASE}/usuarios`),
                fetch(`${API_BASE}/livros`)
            ]);
            if (uRes.ok) setUsers(await uRes.json());
            if (bRes.ok) setBooks(await bRes.json());
        } catch (err) {
            notify("Erro: O backend Java não respondeu.", "error");
        }
    };

    const notify = (text, type = 'success') => {
        setMessage({ text, type });
        setTimeout(() => setMessage({ text: '', type: '' }), 4000);
    };

    const handleGoogleSearch = async () => {
        if (!searchTerm) return;
        setLoading(true);
        try {
            const res = await fetch(`${API_BASE}/google-books/search?titulo=${searchTerm}`);
            const data = await res.json();
            setGoogleBooks(data);
        } catch (err) {
            notify("Erro ao buscar livros externos.", "error");
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
                notify(`Livro "${book.titulo}" importado.`);
                loadInitialData();
            }
        } catch (err) {
            notify("Erro de integração ao salvar obra.", "error");
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
            notify("Erro ao gerar recomendações.", "error");
        }
    };

    return (
        <div className="flex min-h-screen bg-slate-950 text-slate-200 overflow-hidden">

            {/* Sidebar Lateral */}
            <aside className="w-72 bg-slate-900/40 border-r border-slate-800 p-8 flex flex-col h-screen backdrop-blur-md">
                <div className="flex items-center gap-3 text-blue-500 mb-12 px-2">
                    <Library size={40} strokeWidth={2.5} />
                    <h1 className="text-2xl font-black tracking-tighter text-white uppercase italic">ELOTECH LIB</h1>
                </div>

                <nav className="space-y-3 flex-1">
                    <MenuBtn id="dashboard" icon={LayoutDashboard} label="Dashboard" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="usuarios" icon={Users} label="Membros" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="livros" icon={BookOpen} label="Acervo Local" active={activeTab} setter={setActiveTab} />
                    <MenuBtn id="google" icon={Globe} label="Explorar Web" active={activeTab} setter={setActiveTab} />
                </nav>

                <div className="pt-6 border-t border-slate-800 text-[10px] text-slate-600 font-bold uppercase tracking-[0.4em] text-center">
                    Desafio Java 21
                </div>
            </aside>

            {/* Área de Conteúdo */}
            <main className="flex-1 p-12 overflow-y-auto">

                {message.text && (
                    <div className={`fixed top-8 right-8 p-5 rounded-2xl border shadow-2xl flex items-center gap-4 z-50 view-enter ${
                        message.type === 'error' ? "bg-red-950 border-red-800" : "bg-blue-950 border-blue-800"
                    }`}>
                        {message.type === 'error' ? <AlertCircle size={20} /> : <CheckCircle size={20} />}
                        <span className="text-sm font-bold">{message.text}</span>
                    </div>
                )}

                {activeTab === 'dashboard' && (
                    <div className="space-y-12 view-enter">
                        <header>
                            <h2 className="text-6xl font-black text-white tracking-tighter leading-none">Dashboard</h2>
                            <p className="text-slate-500 mt-4 text-xl font-medium">Controle de acervo e membros.</p>
                        </header>
                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-10">
                            <StatCard title="Livros no Acervo" value={books.length} icon={Book} color="text-blue-500" />
                            <StatCard title="Usuários Ativos" value={users.length} icon={Users} color="text-emerald-500" />
                        </div>
                    </div>
                )}

                {activeTab === 'usuarios' && (
                    <div className="space-y-8 view-enter">
                        <h2 className="text-3xl font-bold text-white tracking-tight">Membros Registrados</h2>
                        <div className="bg-slate-900/50 border border-slate-800 rounded-3xl overflow-hidden shadow-2xl">
                            <table className="w-full text-left">
                                <thead className="bg-slate-800/40 text-slate-500 text-[10px] font-bold uppercase tracking-[0.4em]">
                                <tr>
                                    <th className="px-10 py-6">Nome Completo</th>
                                    <th className="px-10 py-6">E-mail</th>
                                    <th className="px-10 py-6 text-right">Ações</th>
                                </tr>
                                </thead>
                                <tbody className="divide-y divide-slate-800/50">
                                {users.map(u => (
                                    <tr key={u.id} className="hover:bg-slate-800/30 transition-colors group">
                                        <td className="px-10 py-6 font-bold text-slate-200">{u.nome}</td>
                                        <td className="px-10 py-6 text-slate-400">
                                            <div className="flex items-center gap-2"><Mail size={14}/> {u.email}</div>
                                        </td>
                                        <td className="px-10 py-6 text-right">
                                            <button
                                                onClick={() => loadRecommendations(u.id)}
                                                className="text-blue-400 hover:text-white flex items-center gap-2 text-xs font-bold float-right bg-blue-500/10 px-4 py-2 rounded-xl transition-all"
                                            >
                                                <Sparkles size={14} /> Recomendação
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                            {users.length === 0 && <div className="p-20 text-center text-slate-700 italic">Banco de dados de usuários vazio.</div>}
                        </div>
                    </div>
                )}

                {activeTab === 'livros' && (
                    <div className="space-y-8 view-enter">
                        <h2 className="text-3xl font-bold text-white tracking-tight">Acervo Local</h2>
                        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-8">
                            {books.map(b => (
                                <div key={b.id} className="bg-slate-900 p-8 rounded-[2rem] border border-slate-800 hover:border-blue-500/40 transition-all group">
                  <span className="text-[10px] font-black bg-blue-500/10 text-blue-400 px-4 py-1.5 rounded-full uppercase tracking-tighter italic">
                    {b.categoria}
                  </span>
                                    <h4 className="text-2xl font-bold text-white mt-4 leading-tight">{b.titulo}</h4>
                                    <p className="text-slate-500 font-medium text-sm">{b.autor}</p>
                                    <div className="mt-8 pt-4 border-t border-slate-800 flex justify-between items-center text-[10px] font-mono text-slate-600 uppercase">
                                        <span>ISBN {b.isbn}</span>
                                        <Check size={16} />
                                    </div>
                                </div>
                            ))}
                        </div>
                        {books.length === 0 && <div className="p-20 text-center text-slate-700 italic">Nenhum livro no acervo. Importe via Google Books.</div>}
                    </div>
                )}

                {activeTab === 'recomendacoes' && (
                    <div className="space-y-10 view-enter">
                        <header className="flex justify-between items-end">
                            <div>
                                <h2 className="text-4xl font-bold text-white tracking-tight flex items-center gap-4">
                                    Sugestões para {selectedUser?.nome} <Sparkles className="text-amber-400" />
                                </h2>
                                <p className="text-slate-500 mt-2 font-medium italic text-lg">Baseado no perfil de categorias do usuário.</p>
                            </div>
                            <button onClick={() => setActiveTab('usuarios')} className="text-slate-400 hover:text-white text-sm font-bold">Voltar para Membros</button>
                        </header>

                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {recommendations.map(b => (
                                <div key={b.id} className="bg-gradient-to-br from-slate-900 to-slate-800 p-8 rounded-[2rem] border border-blue-500/20 shadow-xl">
                                    <span className="text-[10px] font-black text-amber-400 uppercase tracking-widest">{b.categoria}</span>
                                    <h4 className="text-xl font-bold text-white mt-4 leading-tight">{b.titulo}</h4>
                                    <p className="text-slate-400 text-sm mt-1">{b.autor}</p>
                                    <button className="mt-6 w-full py-3 bg-blue-600/20 text-blue-400 rounded-xl font-bold text-xs hover:bg-blue-600 hover:text-white transition-all">
                                        Registrar Interesse
                                    </button>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {activeTab === 'google' && (
                    <div className="space-y-12 view-enter">
                        <header>
                            <h2 className="text-4xl font-bold text-white tracking-tight italic">Google Books API</h2>
                            <p className="text-slate-500 mt-2 text-lg font-medium text-balance">Pesquise obras e adicione-as ao seu catálogo local com um clique.</p>
                        </header>
                        <div className="flex gap-4 p-2 bg-slate-900 border border-slate-800 rounded-2xl shadow-inner focus-within:border-blue-500/50 transition-all">
                            <input
                                className="flex-1 bg-transparent py-4 px-6 outline-none text-white text-lg placeholder-slate-700"
                                placeholder="Busque por título ou autor..."
                                value={searchTerm}
                                onChange={e => setSearchTerm(e.target.value)}
                                onKeyPress={e => e.key === 'Enter' && handleGoogleSearch()}
                            />
                            <button onClick={handleGoogleSearch} className="bg-blue-600 hover:bg-blue-500 text-white px-10 py-4 rounded-xl font-black transition-all disabled:opacity-50" disabled={loading}>
                                {loading ? "..." : "BUSCAR"}
                            </button>
                        </div>
                        <div className="grid grid-cols-1 gap-4">
                            {googleBooks.map((b, i) => (
                                <div key={i} className="bg-slate-900/60 p-6 rounded-2xl border border-slate-800 flex justify-between items-center group hover:bg-slate-800/40 transition-all">
                                    <div>
                                        <h5 className="font-bold text-white text-xl">{b.titulo}</h5>
                                        <p className="text-xs text-slate-500 font-bold uppercase tracking-widest mt-1 italic">{b.autor} • {b.categoria}</p>
                                    </div>
                                    <button onClick={() => handleImport(b)} className="text-xs font-black text-blue-400 hover:text-white border border-blue-400/10 px-8 py-4 rounded-xl hover:bg-blue-600 transition-all shadow-lg">
                                        IMPORTAR
                                    </button>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </main>
        </div>
    );
}

// Sub-componentes Reutilizáveis
function MenuBtn({ id, icon: Icon, label, active, setter }) {
    const isActive = active === id;
    return (
        <button onClick={() => setter(id)} className={`w-full flex items-center gap-4 px-5 py-4 rounded-2xl transition-all duration-300 ${
            isActive ? "bg-blue-600 text-white shadow-xl shadow-blue-900/40 font-bold scale-[1.02]" : "text-slate-500 hover:bg-slate-800/50 hover:text-slate-200"
        }`}>
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
                <p className="text-7xl font-black text-white">{value}</p>
            </div>
            <Icon size={140} className={`${color} opacity-[0.03] absolute -right-8 -bottom-8 group-hover:scale-110 transition-all duration-1000`} />
        </div>
    );
}