package guru.qa.niffler.api.core;

import java.net.*;
import java.util.List;

public enum ThreadSafeCookieStore implements CookieStore {

  INSTANCE;

  private final ThreadLocal<CookieStore> threadLocalCookieStore = ThreadLocal.withInitial(
    this::inMemoryCookieStore
  );

  private CookieStore inMemoryCookieStore() {
    return new CookieManager().getCookieStore();
  }

  @Override
  public void add(URI uri, HttpCookie cookie) {
    getStore().add(uri, cookie);
  }

  @Override
  public List<HttpCookie> get(URI uri) {
    return getStore().get(uri);
  }

  @Override
  public List<HttpCookie> getCookies() {
    return getStore().getCookies();
  }

  @Override
  public List<URI> getURIs() {
    return getStore().getURIs();
  }

  @Override
  public boolean remove(URI uri, HttpCookie cookie) {
    return getStore().remove(uri, cookie);
  }

  @Override
  public boolean removeAll() {
    return getStore().removeAll();
  }

  public String cookieValue(String name) {
    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + getCookies());
    return getCookies().stream()
      .filter(c -> name.equals(c.getName()))
      .map(HttpCookie::getValue)
      .findFirst()
      .orElseThrow();
  }

  private CookieStore getStore() {
    return threadLocalCookieStore.get();
  }
}
