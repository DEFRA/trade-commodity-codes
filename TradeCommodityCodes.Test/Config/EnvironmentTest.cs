using Microsoft.AspNetCore.Builder;

namespace TradeCommodityCodes.Test.Config;

public class EnvironmentTest
{

   [Fact]
   public void IsNotDevModeByDefault()
   { 
       var builder = WebApplication.CreateEmptyBuilder(new WebApplicationOptions());
       var isDev = TradeCommodityCodes.Config.Environment.IsDevMode(builder);
       Assert.False(isDev);
   }
}
