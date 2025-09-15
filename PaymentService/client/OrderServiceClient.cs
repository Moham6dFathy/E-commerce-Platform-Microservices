using System.Net.Http;
using System.Net.Http.Json;
using System.Threading.Tasks;

namespace PaymentService.Services
{
    public interface IOrderServiceClient
    {
        Task<double> GetOrderAmountAsync(int orderId);
    }

    public class OrderServiceClient : IOrderServiceClient
    {
        private readonly HttpClient _httpClient;

        public OrderServiceClient(HttpClient httpClient)
        {
            _httpClient = httpClient;
            _httpClient.BaseAddress = new Uri("http://localhost:8081/"); 
        }

        public async Task<double> GetOrderAmountAsync(int orderId)
        {
            var response = await _httpClient.GetAsync($"api/v1/order/{orderId}/amount");
            response.EnsureSuccessStatusCode();

            var amount = await response.Content.ReadFromJsonAsync<double>();
            return amount;
        }
    }
}
