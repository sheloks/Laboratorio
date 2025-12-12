<template>
  <div class="connection-test">
    <h2>🧪 Prueba de Conexión Backend + Frontend</h2>
    
    <div class="test-buttons">
      <button @click="testPublicEndpoint" :disabled="loading">
        {{ loading ? '🔄 Probando...' : '1. Test Endpoint Público' }}
      </button>
      
      <button @click="testPostRequest" :disabled="loading">
        {{ loading ? '🔄 Probando...' : '2. Test POST Request' }}
      </button>
      
      <button @click="testAuthEndpoints" :disabled="loading">
        {{ loading ? '🔄 Probando...' : '3. Test Autenticación' }}
      </button>
      
      <button @click="clearResults" class="clear-btn">
        🗑️ Limpiar Resultados
      </button>
    </div>

    <!-- Resultados -->
    <div v-if="results.length > 0" class="results">
      <div v-for="(result, index) in results" :key="index" 
           :class="['result', result.status]">
        <h4>{{ result.title }}</h4>
        <div class="status">{{ result.status === 'success' ? '✅' : '❌' }} {{ result.message }}</div>
        <pre v-if="result.data">{{ JSON.stringify(result.data, null, 2) }}</pre>
        <div v-if="result.error" class="error-details">
          {{ result.error }}
        </div>
      </div>
    </div>

    <!-- Resumen -->
    <div v-if="results.length > 0" class="summary">
      <h3>📊 Resumen de Pruebas</h3>
      <p>Exitosas: {{ successCount }} / {{ results.length }}</p>
      <p :class="overallStatus">{{ overallStatus === 'success' ? '🎉 ¡Todas las pruebas pasaron!' : '⚠️ Algunas pruebas fallaron' }}</p>
    </div>
  </div>
</template>

<script>
import api from '../services/api.js';

export default {
  name: 'BackendConnectionTest',
  data() {
    return {
      loading: false,
      results: []
    };
  },
  computed: {
    successCount() {
      return this.results.filter(r => r.status === 'success').length;
    },
    overallStatus() {
      return this.successCount === this.results.length ? 'success' : 'warning';
    }
  },
  methods: {
    async testPublicEndpoint() {
      this.loading = true;
      try {
        const response = await api.get('/api/public/test');
        this.results.push({
          title: 'Endpoint Público (GET)',
          status: 'success',
          message: 'Conexión exitosa con el backend',
          data: response.data
        });
      } catch (error) {
        this.results.push({
          title: 'Endpoint Público (GET)',
          status: 'error',
          message: 'Error en la conexión',
          error: this.getErrorMessage(error)
        });
      } finally {
        this.loading = false;
      }
    },

    async testPostRequest() {
      this.loading = true;
      try {
        const testData = {
          message: 'Hola desde Vue.js! 🎉',
          timestamp: new Date().toISOString(),
          frontend: 'Vue.js 3',
          backend: 'Spring Boot'
        };
        
        const response = await api.post('/api/public/echo', testData);
        this.results.push({
          title: 'Endpoint POST',
          status: 'success',
          message: 'Request POST funcionando correctamente',
          data: response.data
        });
      } catch (error) {
        this.results.push({
          title: 'Endpoint POST',
          status: 'error',
          message: 'Error en request POST',
          error: this.getErrorMessage(error)
        });
      } finally {
        this.loading = false;
      }
    },

    async testAuthEndpoints() {
      this.loading = true;
      try {
        // Test de login (usando credenciales de prueba)
        const loginData = {
          email: 'admin@dental-lab.com',
          password: 'password123'
        };
        
        const response = await api.post('/api/public/simple-login', loginData);
        this.results.push({
          title: 'Autenticación (Login)',
          status: 'success',
          message: 'Sistema de autenticación funcionando',
          data: { 
            user: response.data.user,
            tokenReceived: !!response.data.token
          }
        });
      } catch (error) {
        if (error.response?.status === 401) {
          this.results.push({
            title: 'Autenticación (Login)',
            status: 'warning',
            message: 'Credenciales incorrectas (esperado sin usuario real)',
            error: 'Status 401 - Unauthorized'
          });
        } else {
          this.results.push({
            title: 'Autenticación (Login)',
            status: 'error',
            message: 'Error en autenticación',
            error: this.getErrorMessage(error)
          });
        }
      } finally {
        this.loading = false;
      }
    },

    getErrorMessage(error) {
      if (error.response) {
        return `Status ${error.response.status}: ${error.response.data?.message || error.response.data}`;
      } else if (error.request) {
        return 'Error de red - Backend no responde';
      } else {
        return error.message;
      }
    },

    clearResults() {
      this.results = [];
    }
  }
};
</script>

<style scoped>
.connection-test {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.test-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin: 20px 0;
}

button {
  padding: 12px 16px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

button:not(.clear-btn) {
  background: #2196F3;
  color: white;
}

button:not(.clear-btn):hover:not(:disabled) {
  background: #1976D2;
  transform: translateY(-2px);
}

button:disabled {
  background: #cccccc;
  cursor: not-allowed;
  transform: none;
}

.clear-btn {
  background: #ff9800;
  color: white;
  grid-column: span 2;
}

.clear-btn:hover {
  background: #f57c00;
}

.results {
  margin: 20px 0;
}

.result {
  padding: 15px;
  margin: 10px 0;
  border-radius: 8px;
  border-left: 4px solid;
}

.result.success {
  background: #e8f5e8;
  border-left-color: #4CAF50;
}

.result.error {
  background: #ffebee;
  border-left-color: #f44336;
}

.result.warning {
  background: #fff3e0;
  border-left-color: #ff9800;
}

.result h4 {
  margin: 0 0 8px 0;
  color: #333;
}

.status {
  font-weight: 500;
  margin-bottom: 8px;
}

pre {
  background: rgba(0,0,0,0.05);
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 12px;
}

.error-details {
  background: rgba(244, 67, 54, 0.1);
  padding: 8px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 12px;
}

.summary {
  padding: 15px;
  background: #f5f5f5;
  border-radius: 8px;
  text-align: center;
}

.summary h3 {
  margin: 0 0 10px 0;
}

.summary .success {
  color: #4CAF50;
  font-weight: bold;
}

.summary .warning {
  color: #ff9800;
  font-weight: bold;
}
</style>