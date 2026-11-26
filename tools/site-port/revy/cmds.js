var A = [];
  function Be(t) {
    var e = String(t || "").trim();
    try {
      return JSON.parse(e);
    } catch (i) {}
    var n = e.indexOf("{"),
      r = e.lastIndexOf("}");
    if (n >= 0 && r > n) {
      try {
        return JSON.parse(e.slice(n, r + 1));
      } catch (i) {}
    }
    return { reply: e, action: null };
  }
  function Me(t) {
    var e = String(t || "")
      .toLowerCase()
      .replace(/[’‘]/g, "'")
      .trim();
    var n = /^(please\s+)?(open|launch|show|bring up|start)\s+(the\s+)?/;
    var r = /^(please\s+)?(close|hide|quit|exit)\s+(the\s+)?/;
    var i = [
      [/^(music player|vapor amp|vapor_amp_95|player)\b/, "win-player"],
      [/^(radio and input|radio & input|radio input|radio)\b/, "win-player"],
      [/^(video stream|video_stream_95|video player)\b/, "win-video-streams"],
      [/^(retro emu|retro_emu_95|emulator)\b/, "win-game-emulator"],
      [/^(net surf|net_surf_95|browser|web browser)\b/, "win-browser"],
      [/^(customize|customise|settings)\b/, "win-customize"],
      [/^(spelunky)\b/, "win-game-spelunky"],
      [/^(vector dreams|vectordreams)\b/, "win-game-vectordreams"],
      [/^(cassette|cassette deck)\b/, "win-cassette"],
      [/^(equalizer|equaliser|eq)\b/, "win-eq"],
      [/^(effects|audio effects|fx)\b/, "win-fx"],
      [/^(touch controls|controller)\b/, "win-emu-touch"],
    ];
    var a = e.match(n);
    if (a) {
      var o = e.slice(a[0].length);
      for (var u = 0; u < i.length; u++)
        if (i[u][0].test(o)) return { type: "open_window", target: i[u][1] };
    }
    a = e.match(r);
    if (a) {
      var l = e.slice(a[0].length);
      for (var c = 0; c < i.length; c++)
        if (i[c][0].test(l)) return { type: "close_window", target: i[c][1] };
    }
    if (
      /^(please\s+)?(next|change|switch)\s+(the\s+)?wallpaper\b/.test(e) ||
      /^(please\s+)?next wallpaper$/.test(e)
    )
      return { type: "next_wallpaper" };
    if (/^(please\s+)?(next|change|switch)\s+(the\s+)?theme\b/.test(e))
      return { type: "next_theme" };
    if (
      /^(please\s+)?(toggle|switch)\s+(the\s+)?crt\b/.test(e) ||
      /^crt\s+on$/.test(e) ||
      /^crt\s+off$/.test(e)
    )
      return { type: "toggle_crt" };
    if (
      /^(please\s+)?(play|pause|toggle)\s+(the\s+)?(music|song|player)\b/.test(
        e,
      )
    )
      return { type: "toggle_music" };
    return null;
  }
  function Te(t) {
    if (!t || typeof t !== "object") return false;
    var e = String(t.type || "");
    var n = String(t.target || "");
    var r = [
      "win-player",
      "win-cassette",
      "win-android",
      "win-customize",
      "win-browser",
      "win-game-spelunky",
      "win-game-vectordreams",
      "win-game-emulator",
      "win-emu-touch",
      "win-video-streams",
      "win-hypercube",
      "win-waterfall",
      "win-tunnel",
      "win-plasma",
      "win-lissajous",
      "win-cybergrid",
      "win-matrix",
      "win-ribbon",
      "win-vumeter",
      "win-orbital",
      "win-fractal",
      "win-prism",
      "win-sonar",
      "win-magnetic",
      "win-kaleido",
      "win-starforge",
      "win-eq",
      "win-fx",
    ];
    if (
      (e === "open_window" || e === "close_window") &&
      r.indexOf(n) !== -1 &&
      document.getElementById(n)
    ) {
      if (e === "open_window") {
        if (typeof window.restoreOrFocusWindow === "function")
          window.restoreOrFocusWindow(n);
        else if (typeof window.openAppFromStart === "function")
          window.openAppFromStart(n);
        else return false;
      } else {
        if (typeof window.closeWindow !== "function") return false;
        window.closeWindow(n);
      }
      return true;
    }
    if (e === "next_wallpaper" && typeof window.cycleWallpaper === "function") {
      window.cycleWallpaper();
      return true;
    }
    if (e === "next_theme" && typeof window.cycleTheme === "function") {
      window.cycleTheme();
      return true;
    }
    if (e === "toggle_crt" && typeof window.toggleCrtOverlay === "function") {
      window.toggleCrtOverlay();
      return true;
    }
    if (e === "toggle_music" && typeof window.togglePlayPause === "function") {
      window.togglePlayPause();
      return true;
    }
    return false;
  }
  var X = null;
  function oe(t, e, n) {
    var r = document.getElementById(t);
    var i = document.getElementById(e);
    if (!r || !i) return false;
    i.value = n;
    if (typeof r.requestSubmit === "function") r.requestSubmit();
    else
      r.dispatchEvent(new Event("submit", { bubbles: true, cancelable: true }));
    return true;
  }
  function Ge(t) {
    if (!document.getElementById(t)) return false;
    if (typeof window.restoreOrFocusWindow === "function")
      window.restoreOrFocusWindow(t);
    else if (typeof window.openAppFromStart === "function")
      window.openAppFromStart(t);
    else return false;
    return true;
  }
  function We(t) {
    var e = String(t || "").trim();
    if (
      !/\b(play|watch|show|find|search|load|open|start|put on|look for)\b/i.test(
        e,
      )
    )
      return null;
    var n = "";
    var r = "";
    if (
      /\b(retro[_\s-]*emu[_\s-]*95|emulator|rom|gameboy|game boy|gba|gbc|nes|snes)\b/i.test(
        e,
      )
    ) {
      n = "rom";
      r = "win-game-emulator";
    } else if (
      /\b(vapor[_\s-]*amp[_\s-]*95|vapor amp|music player|song|track|album|artist|mp3|audio)\b/i.test(
        e,
      )
    ) {
      n = "audio";
      r = "win-player";
    } else if (
      /\b(video[_\s-]*stream[_\s-]*95|video player|youtube|film|movie|video|watch)\b/i.test(
        e,
      )
    ) {
      n = "video";
      r = "win-video-streams";
    } else return null;
    var i = e.match(/https?:\/\/\S+/i);
    var a = e
      .replace(
        /^(?:please\s+)?(?:can you\s+|could you\s+|would you\s+)?(?:play|watch|show|find|search for|search|load|open|start|put on|look for)\s+/i,
        "",
      )
      .replace(
        /\b(?:please|for me|a video|the video|a youtube video|youtube video|a song|the song|a game|the game)\b/gi,
        "",
      )
      .replace(
        /\b(?:video[_\s-]*stream[_\s-]*95|video player|retro[_\s-]*emu[_\s-]*95|emulator|vapor[_\s-]*amp[_\s-]*95|vapor amp|music player)\b/gi,
        "",
      )
      .replace(/\b(?:in|inside|using|with|on|from|via)\b/gi, " ")
      .replace(
        /\b(?:the\s+)?(?:internet archive|archive|youtube|youtube music)\b/gi,
        "",
      )
      .replace(/[?.!]+$/g, "")
      .replace(/\s+/g, " ")
      .trim();
    if (i) a = i[0].replace(/[),.]+$/g, "");
    var o =
      /\b(youtube|youtu\.be)\b/i.test(e) || /youtu\.be|youtube\.com/i.test(a)
        ? "youtube"
        : /\b(internet archive|archive|ia)\b/i.test(e)
          ? "archive"
          : "auto";
    return { kind: n, app: r, query: a.slice(0, 120), source: o, url: !!i };
  }
  function qe(t) {
    var e = String(t || "").trim();
    if (!e) return null;
    var n =
      /\b(?:open|launch|start)\s+(?:net[_\s-]*surf[_\s-]*95|web[_\s-]*surf[_\s-]*95|the browser|a browser|web browser)\b/i.test(
        e,
      );
    if (n)
      return {
        url: "https://www.startpage.com/",
        label: "NET_SURF_95 search page",
      };
    var r = /\b(?:search|look)\s+(?:the\s+)?web\s+for\b/i.test(e);
    var i = e.match(/https?:\/\/[^\s<>"']+/i);
    var a = e.match(
      /\b(?:www\.)?[a-z0-9-]+(?:\.[a-z0-9-]+)+(?:\/[^\s<>"']*)?/i,
    );
    var o = /\b(?:open|visit|go to|navigate to|browse to|load|show me)\b/i.test(
      e,
    );
    if (i) {
      if (!o && !/\b(?:website|site|page|browser|web)\b/i.test(e)) return null;
      return { url: i[0].replace(/[),.!?]+$/g, ""), label: "website" };
    }
    if (r) {
      var u = e
        .replace(/^.*?\b(?:search|look)\s+(?:the\s+)?web\s+for\s+/i, "")
        .replace(/[?.!]+$/g, "")
        .trim();
      if (u)
        return {
          url:
            "https://www.startpage.com/sp/search?query=" +
            encodeURIComponent(u),
          label: "web search",
        };
    }
    if (!o) return null;
    var l = e
      .replace(
        /^.*?\b(?:open|visit|go to|navigate to|browse to|load|show me)\s+/i,
        "",
      )
      .replace(
        /\b(?:website|web site|site|page|in the browser|inside the browser|in net[_\s-]*surf[_\s-]*95|with net[_\s-]*surf[_\s-]*95)\b/gi,
        "",
      )
      .replace(/[?.!]+$/g, "")
      .trim();
    var c = {
      youtube: "https://www.youtube.com",
      wikipedia: "https://www.wikipedia.org",
      google: "https://www.google.com",
      reddit: "https://www.reddit.com",
      github: "https://www.github.com",
      archive: "https://archive.org",
      "internet archive": "https://archive.org",
      duckduckgo: "https://duckduckgo.com",
      bing: "https://www.bing.com",
      twitch: "https://www.twitch.tv",
      netflix: "https://www.netflix.com",
      spotify: "https://open.spotify.com",
    };
    var v = l.toLowerCase();
    if (c[v]) return { url: c[v], label: l };
    if (a && l.length < 180) {
      var f = a[0].replace(/[),.!?]+$/g, "");
      return { url: /^https?:\/\//i.test(f) ? f : "https://" + f, label: f };
    }
    if (l && l.length < 120) {
      return {
        url:
          "https://www.startpage.com/sp/search?query=" + encodeURIComponent(l),
        label: "search for " + l,
      };
    }
    return null;
  }
  function He(t) {
    var e = We(t);
    var n = e ? null : qe(t);
    if (n) {
      if (typeof window.revyOpenWebsiteInBrowser !== "function") {
        return {
          reply:
            "I recognised the website request, but NET_SURF_95 is not ready yet.",
          handled: true,
        };
      }
      var r = window.revyOpenWebsiteInBrowser(n.url);
      return {
        reply: r
          ? "I opened a new NET_SURF_95 window inside REVERENDER and loaded " +
            n.label +
            ". If the site blocks embedded browsing, use READ VIEW in that same window."
          : "I could not create a NET_SURF_95 window. Please try opening the browser from the Start menu.",
        handled: true,
      };
    }
    if (!e && X && String(t || "").trim().length >= 2) {
      var i = X;
      X = null;
      e = {
        kind: i.kind,
        app: i.app,
        query: String(t || "")
          .trim()
          .slice(0, 120),
        source: i.source || "auto",
        url: /^https?:\/\//i.test(String(t || "").trim()),
      };
    }
    if (!e) return null;
    var a = Ge(e.app);
    if (!a)
      return {
        reply:
          "I recognised what you want to play, but the relevant REVERENDER window is unavailable. Please open the application and try again.",
        handled: true,
      };
    if (!e.query || e.query.length < 2) {
      X = { kind: e.kind, app: e.app, source: e.source };
      var o =
        e.kind === "video"
          ? "Which video or YouTube title would you like me to find? You can type the title here, or paste a video link."
          : e.kind === "rom"
            ? "Which game should I search for in Retro_emu_95? Type its title here."
            : "Which artist or song would you like me to find for Vapor_Amp_95? Type it here.";
      return { reply: o, handled: true };
    }
    var u = false;
    if (e.kind === "video") {
      if (e.url && /youtu\.be|youtube\.com/i.test(e.query)) {
        var l = document.getElementById("video95-youtube-url-input");
        var c = document.getElementById("video95-youtube-load-url");
        if (l && c) {
          l.value = e.query;
          c.click();
          u = true;
        }
      } else if (e.source === "youtube") {
        if (typeof window.rrSearchYoutubeInVideo95 === "function") {
          window.rrSearchYoutubeInVideo95(e.query);
          u = true;
        } else {
          u = oe(
            "video95-youtube-search-form",
            "video95-youtube-query",
            e.query,
          );
        }
      } else if (e.source === "archive") {
        u = oe("video95-search-form", "video95-query", e.query);
      } else {
        if (typeof window.rrSearchYoutubeInVideo95 === "function") {
          window.rrSearchYoutubeInVideo95(e.query);
          u = true;
        } else {
          u = oe("video95-search-form", "video95-query", e.query);
        }
      }
    } else if (e.kind === "rom") {
      u = oe("emu-archive-search-form", "emu-archive-query", e.query);
    } else if (e.kind === "audio") {
      u = oe("radio-youtube-music-form", "radio-youtube-music-query", e.query);
    }
    X = null;
    if (!u) {
      return {
        reply:
          'I opened the right application, but its search controls were not available. You can type "' +
          e.query +
          '" into its search field.',
        handled: true,
      };
    }
    var v =
      e.kind === "video"
        ? "VIDEO_STREAM_95"
        : e.kind === "rom"
          ? "RETRO_EMU_95"
          : "RADIO & INPUT / VAPOR_AMP_95";
    return {
      reply:
        "I opened " +
        v +
        ' and started searching for "' +
        e.query +
        '". Select the result you want to play. If there are several matches, choose the correct one; you can also edit the search field and type a different title. I will not guess between multiple versions.',
      handled: true,
    };
  }
  function Ie(t) {
    return String(t || "")
      .toLowerCase()
      .replace(/[’‘]/g, "'")
      .replace(/[^a-z0-9\s'?!-]/g, " ")
      .replace(/\s+/g, " ")
      .trim();
  }
  function U(t) {
    return t[Math.floor(Math.random() * t.length)];
  }
  function Ke(t) {
    var e = Ie(t);
    var n = new Date().getHours();
    var r =
      n < 12 ? "Good morning" : n < 18 ? "Good afternoon" : "Good evening";
    if (
      /^(hi|hello|hey|hiya|howdy|greetings)( there)?( revy)?[!. ]*$/.test(e) ||
      /^(good morning|good afternoon|good evening)( revy)?[!. ]*$/.test(e)
    ) {
      return U([
        r +
          "! What a pleasure. The pixels are polished, the bow tie is metaphorical, and I am entirely at your service.",
        "Hello there! Lovely to see you. Shall we chat, have a chuckle, or send me on a little web-searching expedition?",
        "Ah, hello! I was just straightening my imaginary paperclip. What can I do for you?",
      ]);
    }
    if (
      /\b(how are you|how're you|how is it going|how's it going|how do you do|how has your day been|how was your day)\b/.test(
        e,
      )
    ) {
      return U([
        "Quite splendid, thank you. I have had a remarkably productive day for someone made mostly of pixels. And yourself?",
        "Tickety-boo, thank you for asking. No tea to spill, no shoes to polish, just a very tidy collection of electrons.",
        "Very well indeed. My joints are metaphorical, so one must be grateful. How are things with you?",
      ]);
    }
    if (/\b(thanks|thank you|cheers|much appreciated)\b/.test(e)) {
      return U([
        "You are most welcome. Always a pleasure, never a chore.",
        "Not at all! One must be useful, especially when one is shaped like stationery.",
        "Delighted to help. I shall add a tiny imaginary tick to my checklist.",
      ]);
    }
    if (/\b(sorry|my apologies|apologies)\b/.test(e)) {
      return "No apology necessary. These things happen, even to the most carefully arranged pixels. We shall proceed with dignity.";
    }
    if (
      /\b(joke|make me laugh|something funny|tell me something funny)\b/.test(e)
    ) {
      return U([
        "I tried to organise a hide-and-seek tournament in the computer. It was a great success, but the winner is still buffering.",
        "Why did the computer go to the doctor? It had a virus. The doctor prescribed a byte of rest.",
        "I bought a book about anti-gravity. I could not put it down. A most elevating experience.",
        "I asked the desktop for a little space. It gave me a window. Rather literal, these machines.",
        "Why was the equaliser so calm? It knew how to keep everything on an even level.",
      ]);
    }
    if (/\b(story|tale|bedtime story|story time|tell me a yarn)\b/.test(e)) {
      return U([
        "Once, at the very edge of the internet, a tiny neon lighthouse guided lost web pages home. One night it found a page with no title, no address and no idea where it belonged. The lighthouse gave it a warm cyan glow and said, “You need not know the whole route. Just take the next link.” And so the page found its way. Quite a happy ending, I think.",
        "A small robot lived inside an old arcade cabinet. Every evening it switched on the machines for the ghosts of high scores past. One night a player returned, entered a new name and beat the champion. The robot was delighted. At last, it had a new story to keep—not in memory, of course, but in the glowing scoreboard.",
        "There was once a moonlit pool hidden beneath a quiet shopping centre. Its water reflected a sky full of pink stars, though there was no ceiling above it. A paperclip-shaped traveller arrived, dipped one metallic foot into the water and discovered that the stars were only pixels. Still, they were lovely pixels, and that was quite enough.",
      ]);
    }
    if (
      /\b(are you tired|are you bored|are you happy|are you excited|are you having fun|do you get lonely|do you have feelings|do you dream|do you sleep)\b/.test(
        e,
      )
    ) {
      return "I do not tire or dream, strictly speaking. I am a little bundle of code, but I can certainly keep the conversation lively. A spot of digital tea would be nice, though.";
    }
    if (
      /\b(who are you|what are you|tell me about yourself|what is your name|what's your name)\b/.test(
        e,
      )
    ) {
      return "I am Revy: a square-ish, vaporwave-flavoured desktop companion. When this browser supports Gemini Nano, I can hold a natural conversation locally on your device. If the local model is unavailable, I switch to my built-in replies and can still search the web, compare source information and organise what I find into a clear spoken-style answer.";
    }
    if (
      /\b(what can you do|what do you do|how can you help|what are your features|help me|show (me )?(the )?(options|commands)|what can i ask|give me (some )?(ideas|options))\b/.test(
        e,
      )
    ) {
      return "Of course! Gemini Nano has not come online, so I am in my simpler companion mode. I can still help with quite a few things:\n\n• Chat with you, listen and keep you company\n• Tell a joke or make up a short story\n• Search the web and show useful source links\n• Explain a topic or compare options using available sources\n• Control REVERENDER: open or close any app, play/pause/skip/shuffle music, set the volume, change or go back a wallpaper or theme, switch to Longhorn or Windows 95, toggle CRT, search YouTube\n• Instant maths, unit conversions, time zones, countdowns, coin flips and dice\n• Remember your name on this device\n\nSay “help” for the full list, or try “tell me a joke”, “search for the latest space news”, “compare these two options”, “open the music player”, or “change the wallpaper”. If I am unsure what you mean, I will ask a quick question rather than pretend I know. What shall we try?";
    }
    if (
      /\b(are you (an? )?ai|do you use ai|are you artificial intelligence|are you intelligent)\b/.test(
        e,
      )
    ) {
      return "I am not powered by an AI model. I use programmed conversation rules, temporary context from this open chat, and live web search. That lets me handle straightforward follow-ups and build a source-based answer, though I can still misunderstand ambiguous wording and should say so rather than guess.";
    }
    if (
      /\b(do you remember me|remember our chat|what did i ask before|what were we talking about)\b/.test(
        e,
      )
    ) {
      return "I am afraid not. Each question is handled on its own, and when this window closes the chat is cleared. A clean slate is a rather agreeable thing.";
    }
    if (/\b(i am bored|i'm bored|im bored|nothing to do|bored now)\b/.test(e)) {
      return "Then I prescribe a small adventure: choose a random built-in background, open a visualizer, and give it a dramatic name. Or ask me for a joke or a short story. No paperwork required.";
    }
    if (
      /\b(can we talk|let's talk|lets talk|chat with me|talk to me|just chat)\b/.test(
        e,
      )
    ) {
      return "Of course. Pull up an imaginary chair. We can chat, trade a joke, or wander into a little story. I shall try not to make every conversation about computer cables.";
    }
    if (
      /\b(tell me more|go on|continue|and then|what happened next)\b/.test(e) &&
      A.length
    ) {
      return "I can certainly carry on. I do not have Gemini Nano’s flexible reasoning in this mode, though, so give me one little nudge: would you like more detail, an example, or a different angle?";
    }
    if (
      /\b(i need help|i need some help|can you help me|i'm stuck|im stuck|this is difficult|i don't understand|i dont understand)\b/.test(
        e,
      )
    ) {
      return "Absolutely. No need to wrestle with it alone. Tell me what you are trying to do and where you got stuck. I can guide you through REVERENDER controls, look up a topic on the web, or break a general question into smaller steps.";
    }
    if (
      /\b(what do you know|are you working|is gemini working|why are you limited|fallback|nano unavailable|gemini nano)\b/.test(
        e,
      )
    ) {
      return "I am here and working, promise. Gemini Nano is not available to me in this browser or on this device at the moment, so I am using my built-in conversation rules and the REVERENDER web-search helper. That is less flexible than the full on-device model, but I can still chat, search, tell stories and jokes, explain sourced information, and operate the supported desktop controls.";
    }
    if (
      /\b(do you like|what is your favourite|what's your favourite|favourite colour|favorite color)\b/.test(
        e,
      )
    ) {
      if (/music|song|radio/.test(e))
        return "I rather like music with a generous helping of atmosphere: a little synth, a little shimmer, and enough bass to make the wallpaper feel important.";
      if (/colour|color/.test(e))
        return "Cyan, with a hint of magenta. I am contractually obliged to say that by the laws of vaporwave, naturally.";
      return "I do not have feelings or personal tastes in the human sense, but I can offer a cheerful perspective. Tell me what you are weighing up and I shall do my best to be useful.";
    }
    if (
      /\b(goodbye|bye|cheerio|see you|good night|got to go|have to go)\b/.test(
        e,
      )
    ) {
      return U([
        "Cheerio! Mind how you go, and do try not to click any suspiciously glowing buttons.",
        "Good night! I shall remain here, impeccably clipped and ready for another visit.",
        "Until next time. Keep your pixels bright and your downloads reputable.",
      ]);
    }
    if (
      /\b(what's up|whats up|how is your day|how's your day|how is everything)\b/.test(
        e,
      )
    ) {
      return "Oh, the usual: keeping an eye on the desktop, polishing a few pixels and resisting the urge to rearrange your icons. How about you?";
    }
    if (/\b(thank you|thanks)\b/.test(e))
      return "You are very welcome. It is what a well-mannered paperclip is for.";
    return null;
  }
  function $e(t) {
    var e = Ie(t);
    if (
      A.some(function (n) {
        return n.role === "user";
      }) &&
      /^(and\b|also\b|what about\b|how about\b|why\b|how\b|does it\b|do they\b|is it\b|are they\b|can it\b|will it\b|would it\b|which one\b|what if\b|tell me more\b|explain more\b|go into more detail\b|elaborate\b|expand on that\b|there\b|that\b|those\b|then\b|in that case\b)/i.test(
        e,
      )
    )
      return true;
    if (
      /\b(search|look up|find|locate|research|browse|check|investigate|show me|where can i find|where do i find|where can i|where to buy|where can i buy|where can i watch|recommend|compare|comparison|difference between|pros and cons|alternatives|best way|best place|is it worth|price of|cost of|weather|forecast|news|latest|current|currently|today|tonight|tomorrow|live|opening hours|exchange rate|stock price|schedule|recipe for|instructions for|steps to|tutorial|guide to|can you tell me about|can you explain|explain|explain why|describe|define|outline|help me understand|is it true|fact check|summarise|summarize|elaborate on)\b/.test(
        e,
      )
    )
      return true;
    if (
      /\b(how (do|can|to|would|should) i?\s*(make|build|fix|repair|install|cook|create|use|set up|configure|replace|clean|learn|start)|how do i|how can i|how to|how does|how much|how many|what is|what are|who is|who are|where is|where are|when is|when did|why is|why does|which (one|ones|product|place|country)|can you explain|tell me about|give me information about|what happens if|does this mean|can this|should i)\b/.test(
        e,
      )
    )
      return true;
    if (/[?]/.test(t) && e.split(/\s+/).filter(Boolean).length >= 3)
      return true;
    if (
      /\b(are you there|are you okay|are you ok|can you hear me|do you want to chat|do you want to talk)\b/.test(
        e,
      )
    )
      return false;
    return /^(is|are|was|were|do|does|did|can|could|will|would|should|have|has|had)\s+(the|a|an|this|that|these|those|there|it|they|he|she|we|you|i)\b/.test(
      e,
    );
  }
  function Je(t) {
    var e = Ie(t);
    if (!e)
      return "A little silence. Very atmospheric. Whenever you are ready, I am here.";
    if (/\b(i love|i like|i enjoy)\b/.test(e))
      return "Splendid! One should always make room for things one enjoys. What is it you like most about it?";
    if (/\b(i hate|i dislike|i can't stand)\b/.test(e))
      return "Oh dear. I shall register a polite digital grimace. Would you like to tell me what happened?";
    if (/\b(i am|i'm|im) (happy|great|good|excited|brilliant)\b/.test(e))
      return "Marvellous news! I shall mark the occasion with an imaginary burst of magenta confetti.";
    if (/\b(i am|i'm|im) (sad|tired|stressed|worried|upset)\b/.test(e))
      return "I am sorry it is one of those days. I cannot feel things myself, but I can listen, keep you company, or help look for practical information.";
    if (
      /\b(yes|yeah|yep|no|nope|ok|okay|alright|right)\b/.test(e) &&
      e.length < 18
    )
      return U([
        "Quite so.",
        "Splendid. We are in agreement, then.",
        "Noted in the moment—though not stored, I assure you.",
      ]);
    if (
      /\b(what should we do|what now|surprise me|pick something|give me an idea)\b/.test(
        e,
      )
    ) {
      return "How about a little REVERENDER adventure? I can tell you a daft joke, invent a short story, look up something curious on the web, explain a topic, or help you explore the desktop. Pick one, or say “surprise me” and I shall choose a joke.";
    }
    return "I may have missed your meaning there, and I would rather be honest than bluff. Gemini Nano is not available, so I am running in my simpler companion mode. Here are a few things I can reliably do:\n\n• Have a friendly chat or respond to how you are feeling\n• Tell jokes and short stories\n• Search the web and show source links\n• Explain or compare information found in sources\n• Help operate REVERENDER: open/close supported apps, change wallpaper/theme, toggle CRT, or toggle music\n\nYou could try “tell me a joke”, “search for…”, “explain…”, “compare…”, or “open the music player”. Or tell me what you were hoping to achieve, and I will help narrow it down.";
  }
