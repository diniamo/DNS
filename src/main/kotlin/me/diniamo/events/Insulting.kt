package me.diniamo.events

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.ThreadLocalRandom

class Insulting : ListenerAdapter() {
    private val insults = arrayOf("My phone battery lasts longer than your relationships.",
            "Oh you’re talking to me, I thought you only talked behind my back.",
            "Don’t you get tired of putting make up on two faces every morning?",
            "Too bad you can’t count jumping to conclusions and running your mouth as exercise.",
            "Is your drama going to an intermission soon?",
            "I’m an acquired taste. If you don’t like me, acquire some taste.",
            "If I wanted a bitch, I would have bought a dog.",
            "My business is my business. Unless you’re a thong, get out of my ass.",
            "It’s a shame you can’t Photoshop your personality.",
            "I don’t sugarcoat shit. I’m not Willy Wonka.",
            "Acting like a prick doesn’t make yours grow bigger.",
            "The smartest thing that ever came out of your mouth was a penis.",
            "Calm down. Take a deep breath and then hold it for about twenty minutes.",
            "Jealousy is a disease. Get well soon, bitch!",
            "When karma comes back to punch you in the face, I want to be there in case it needs help.",
            "You have more faces than Mount Rushmore.",
            "Sorry, sarcasm falls out of my mouth like bullshit falls out of yours.",
            "Don’t mistake my silence for weakness. No one plans a murder out loud.",
            "I’m sorry you got offended that one time you were treated the way you treat everyone all the time.",
            "You should wear a condom on your head. If you’re going to be a dick, you might as well dress like one.",
            "Maybe you should eat make-up so you’ll be pretty on the inside too.",
            "Being a bitch is a tough job but someone has to do it.",
            "My middle finger gets a boner every time I see you.",
            "Whoever told you to be yourself gave you really bad advice.",
            "If I had a face like yours I’d sue my parents.",
            "I thought I had the flu, but then I realized your face makes me sick to my stomach.",
            "I’m jealous of people who don’t know you.",
            "I’m sorry that my brutal honesty inconvenienced your ego.",
            "You sound reasonable… Time to up my medication.",
            "Aww, it’s so cute when you try to talk about things you don’t understand.",
            "Is there an app I can download to make you disappear?",
            "I’m visualizing duck tape over your mouth.",
            "I suggest you do a little soul searching. You might just find one.",
            "Some people should use a glue stick instead of chapstick.",
            "I’d smack you, but that would be animal abuse.",
            "Why is it acceptable for you to be an idiot but not for me to point it out?",
            "If you’re offended by my opinion, you should hear the ones I keep to myself.",
            "If you’re going to be a smart ass, first you have to be smart, otherwise you’re just an ass.",
            "Your face is fine but you will have to put a bag over that personality.",
            "It’s scary to think people like you are allowed to vote.",
            "I’m sorry, what language are you speaking? It sounds like bullshit.",
            "Everyone brings happiness to a room. I do when I enter, you do when you leave.",
            "I keep thinking you can’t get any dumber and you keep proving me wrong.",
            "You’re like a plunger. You like to bring up old shit.",
            "I am not ignoring you. I am simply giving you time to reflect on what an idiot you are being.",
            "Your birth certificate is an apology letter from the condom manufacturer.")

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) return
        if (ThreadLocalRandom.current().nextInt(1, 101) < 3/*++insultRate*/) {
            event.channel.sendMessage(insults[ThreadLocalRandom.current().nextInt(insults.size)]).queue()
        }
    }
}