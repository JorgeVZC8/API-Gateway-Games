const { createApp, reactive, ref, onMounted, computed } = Vue;

createApp({
  setup() {
    const gatewayBase = 'http://localhost:8082';
    const token = ref('');
    const games = ref([]);
    const activeView = ref('save');

    const credentials = reactive({ email: '', password: '' });
    const formGame = reactive({ id: '', name: '' });
    const searchId = ref('');
    const foundGame = ref(null);
    const editId = ref('');
    const editGame = ref(null);
    const deleteId = ref('');

    const status = reactive({ message: '', type: '' });
    const loading = reactive({
      auth: false,
      games: false,
      create: false,
      search: false,
      editLoad: false,
      update: false,
      delete: false,
    });

    const isAuthenticated = computed(() => Boolean(token.value));

    const setStatus = (message, type = 'ok') => {
      status.message = message;
      status.type = type;
    };

    const parseBackendMessage = async (response, fallbackMessage) => {
      const contentType = response.headers.get('content-type') ?? '';
      const bodyText = await response.text();

      if (!bodyText) return fallbackMessage;

      if (contentType.includes('application/json')) {
        try {
          const parsed = JSON.parse(bodyText);
          return parsed.message ?? parsed.error ?? bodyText;
        } catch {
          return bodyText;
        }
      }

      return bodyText;
    };

    const authHeaders = () => {
      if (!token.value) {
        throw new Error('No hay token. Debes registrarte para consumir /v1/games.');
      }
      return {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token.value}`,
      };
    };

    const registerAndStoreToken = async () => {
      loading.auth = true;
      try {
        const response = await fetch(`${gatewayBase}/v1/auth/register`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(credentials),
        });

        if (!response.ok) {
          const message = await parseBackendMessage(response, `Error ${response.status} al registrar usuario`);
          throw new Error(message);
        }

        const payload = await response.json();
        const backendToken = payload.token ?? payload.jwt ?? payload.accessToken ?? '';

        if (!backendToken) {
          throw new Error('El backend no devolvió token en /v1/auth/register');
        }

        token.value = backendToken;
        localStorage.setItem('jwtToken', backendToken);
        setStatus('Registro correcto: token obtenido. Ya puedes operar en /v1/games.', 'ok');
        await fetchGames();
      } catch (error) {
        setStatus(error.message, 'error');
      } finally {
        loading.auth = false;
      }
    };

    const clearSession = () => {
      token.value = '';
      localStorage.removeItem('jwtToken');
      games.value = [];
      foundGame.value = null;
      editGame.value = null;
      setStatus('Sesión cerrada. Debes registrarte de nuevo para usar /v1/games.', 'ok');
    };

    const setView = (view) => {
      activeView.value = view;
      foundGame.value = null;
      setStatus('', '');
    };

    const fetchGames = async () => {
      loading.games = true;
      try {
        const response = await fetch(`${gatewayBase}/v1/games`, {
          headers: authHeaders(),
        });

        if (!response.ok) {
          const message = await parseBackendMessage(response, `Error ${response.status} al listar videojuegos`);
          throw new Error(message);
        }

        games.value = await response.json();
        setStatus('Videojuegos cargados correctamente', 'ok');
      } catch (error) {
        setStatus(error.message, 'error');
      } finally {
        loading.games = false;
      }
    };

    const createGame = async () => {
      loading.create = true;
      try {
        const response = await fetch(`${gatewayBase}/v1/games`, {
          method: 'POST',
          headers: authHeaders(),
          body: JSON.stringify({
            id: Number(formGame.id),
            name: formGame.name,
          }),
        });

        if (!response.ok) {
          const message = await parseBackendMessage(response, `Error ${response.status} al guardar videojuego`);
          throw new Error(message);
        }

        formGame.id = '';
        formGame.name = '';
        await fetchGames();
        setStatus('Videojuego guardado correctamente', 'ok');
      } catch (error) {
        setStatus(error.message, 'error');
      } finally {
        loading.create = false;
      }
    };

    const searchGame = async () => {
      loading.search = true;
      foundGame.value = null;
      try {
        const response = await fetch(`${gatewayBase}/v1/games/${searchId.value}`, {
          headers: authHeaders(),
        });

        if (!response.ok) {
          const message = await parseBackendMessage(response, `Error ${response.status} al buscar videojuego`);
          throw new Error(message);
        }

        foundGame.value = await response.json();
        setStatus('Videojuego encontrado', 'ok');
      } catch (error) {
        setStatus(error.message, 'error');
      } finally {
        loading.search = false;
      }
    };

    const loadGameToEdit = async () => {
      loading.editLoad = true;
      editGame.value = null;
      try {
        const response = await fetch(`${gatewayBase}/v1/games/${editId.value}`, {
          headers: authHeaders(),
        });

        if (!response.ok) {
          const message = await parseBackendMessage(response, `Error ${response.status} al cargar videojuego`);
          throw new Error(message);
        }

        editGame.value = await response.json();
        setStatus('Videojuego cargado para edición', 'ok');
      } catch (error) {
        setStatus(error.message, 'error');
      } finally {
        loading.editLoad = false;
      }
    };

    const updateGame = async () => {
      loading.update = true;
      try {
        const response = await fetch(`${gatewayBase}/v1/games/${editGame.value.id}`, {
          method: 'PUT',
          headers: authHeaders(),
          body: JSON.stringify({
            name: editGame.value.name,
          }),
        });

        if (!response.ok) {
          const message = await parseBackendMessage(response, `Error ${response.status} al editar videojuego`);
          throw new Error(message);
        }

        editGame.value = null;
        editId.value = '';
        await fetchGames();
        setStatus('Videojuego actualizado correctamente', 'ok');
      } catch (error) {
        setStatus(error.message, 'error');
      } finally {
        loading.update = false;
      }
    };

    const deleteGame = async () => {
      loading.delete = true;
      try {
        const response = await fetch(`${gatewayBase}/v1/games/${deleteId.value}`, {
          method: 'DELETE',
          headers: authHeaders(),
        });

        if (!response.ok) {
          const message = await parseBackendMessage(response, `Error ${response.status} al eliminar videojuego`);
          throw new Error(message);
        }

        const successMessage = await parseBackendMessage(response, 'Videojuego eliminado correctamente');
        deleteId.value = '';
        await fetchGames();
        setStatus(successMessage, 'ok');
      } catch (error) {
        setStatus(error.message, 'error');
      } finally {
        loading.delete = false;
      }
    };

    onMounted(() => {
      localStorage.removeItem('jwtToken');
      setStatus('Primero regístrate para obtener token. Sin eso, /v1/games debe fallar.', 'error');
    });

    return {
      gatewayBase,
      credentials,
      games,
      activeView,
      formGame,
      searchId,
      foundGame,
      editId,
      editGame,
      deleteId,
      status,
      loading,
      isAuthenticated,
      registerAndStoreToken,
      clearSession,
      setView,
      fetchGames,
      createGame,
      searchGame,
      loadGameToEdit,
      updateGame,
      deleteGame,
    };
  },
}).mount('#app');
